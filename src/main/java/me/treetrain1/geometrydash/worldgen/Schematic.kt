package me.treetrain1.geometrydash.worldgen

import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.resources.FileToIdConverter
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.phys.Vec3
import java.io.IOException
import kotlin.jvm.optionals.getOrNull

/**
 * Class representing the data of a block.
 * @property blockState The state of the block.
 * @property blockEntityData The data of the block entity.
 */
class BlockData(
    var blockState: BlockState? = null,
    var blockEntityData: CompoundTag? = null
) {
    inline val hasBlockState: Boolean get() = this.blockState != null
    inline val hasBlockEntity: Boolean get() = this.blockEntityData != null
}

/**
 * Class representing a Sponge V3 Schematic.
 * @property length The length of the schematic.
 * @property width The width of the schematic.
 * @property height The height of the schematic.
 * @property blockDatas The data of the blocks in the schematic.
 * @property entities The entities in the schematic.
 */
class Schematic(
    @JvmField
    val length: Int,
    @JvmField
    val width: Int,
    @JvmField
    val height: Int,
    @JvmField
    val blockDatas: Array<Array<Array<BlockData?>>>,
    @JvmField
    val entities: List<CompoundTag>
) {

    companion object {
        private val CONVERTER = FileToIdConverter("schematics", ".schem")

        /**
         * Load a schematic from a resource.
         * @param level The server level.
         * @param id The resource location.
         * @return The loaded schematic.
         */
        fun loadFromResource(level: ServerLevel, id: ResourceLocation): Schematic
            = loadFromResource(level.server, id)

        /**
         * Load a schematic from a resource.
         * @param server The Minecraft server.
         * @param id The resource location.
         * @return The loaded schematic.
         */
        fun loadFromResource(server: MinecraftServer, id: ResourceLocation): Schematic {
            val resourceManager = server.resourceManager
            val resourceLocation = CONVERTER.idToFile(id)
            return load(resourceManager, resourceLocation)
        }

        /**
         * Load a schematic from a resource.
         * @param resourceManager The resource manager.
         * @param resourceLocation The resource location.
         * @return The loaded schematic.
         */
        private fun load(resourceManager: ResourceManager, resourceLocation: ResourceLocation): Schematic {
            try {
                resourceManager.open(resourceLocation).use { inputStream ->

                    try {
                        val tag = NbtIo.readCompressed(inputStream, NbtAccounter.unlimitedHeap())
                        return readTag(tag)
                    } catch (e: Throwable) {
                        if (inputStream != null)
                            try {
                                inputStream.close()
                            } catch (e2: Throwable) {
                                e.addSuppressed(e2)
                            }

                        throw e
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        /**
         * Read a block state from a string.
         * @param stateStr The string to read from.
         * @return The read block state.
         */
        fun read(stateStr: String): BlockState {
            val properties: MutableMap<String, String> = hashMapOf()
            var str = stateStr
            val start = stateStr.indexOf(91.toChar(), 0)
            val stop = stateStr.indexOf(93.toChar(), 0)
            if (start != -1) {
                str = stateStr.substring(0, start)
                if (stop < start) {
                    return Blocks.AIR.defaultBlockState()
                }

                var index = start + 1
                var key = ""
                var currString = StringBuilder()
                while (index <= stop) {
                    val currChar: Char = stateStr[index]
                    if (currChar == '=') {
                        key = currString.toString()
                        currString = StringBuilder()
                    } else if (currChar != ',' && currChar != ']') {
                        currString.append(stateStr[index])
                    } else {
                        properties[key] = currString.toString()
                        currString = StringBuilder()
                    }
                    ++index
                }
            }

            var blockState = try {
                BuiltInRegistries.BLOCK.get(ResourceLocation(str)).defaultBlockState()
            } catch (e: Exception) {
                Blocks.AIR.defaultBlockState()
            }

            if (properties.isNotEmpty()) {
                blockState = getCustomBlockState<Comparable<Comparable<*>>>(blockState, properties)
            }

            return blockState
        }

        /**
         * Get a custom block state.
         * @param blockState The base block state.
         * @param properties The properties to apply.
         * @return The custom block state.
         */
        @Suppress("UNCHECKED_CAST")
        private fun <T : Comparable<T>> getCustomBlockState(blockState: BlockState, properties: Map<String, String>): BlockState {
            var state = blockState

            for ((key, value) in properties.entries) {
                for (property in state.properties) {
                    if (property.name != key) continue
                    val propVal = property.getValue(value).orElse(null)
                    if (propVal != null) {
                        state = state.setValue(property as Property<T>, propVal as T)
                        break
                    }
                }
            }
            return state
        }

        /**
         * Read a schematic from a tag.
         * @param tag The tag to read from.
         * @return The read schematic.
         */
        private fun readTag(tag: CompoundTag): Schematic {
            val length = tag.getShort("Length").toInt()
            val width = tag.getShort("Width").toInt()
            val height = tag.getShort("Height").toInt()
            val offset = tag.getIntArray("Offset")
            val offsetPos = BlockPos(offset[0], offset[1], offset[2])

            val palette: MutableMap<Int, BlockState> = hashMapOf()
            val paletteTag = tag.getCompound("Palette")

            // read palette
            for (stateKey in paletteTag.allKeys) {
                val blockState = read(stateKey)
                val id = paletteTag.getInt(stateKey)
                palette[id] = blockState
            }

            val blockData: Array<Array<Array<BlockData?>>> = Array(width) { Array(height) { arrayOfNulls(length) } }
            readBlockData(tag.getByteArray("BlockData"), palette, length, width, blockData)
            readBlockEntityData(tag.getList("BlockEntities", CompoundTag.TAG_COMPOUND.toInt()), blockData)
            val entities: List<CompoundTag> = readEntityData(tag.getList("Entities", CompoundTag.TAG_COMPOUND.toInt()), offsetPos)
            return Schematic(length, width, height, blockData, entities)
        }

        /**
         * Read block data from a byte array.
         * @param blockData The byte array to read from.
         * @param palette The palette to use.
         * @param length The length of the schematic.
         * @param width The width of the schematic.
         * @param blockDatas The array to store the read block data in.
         */
        private fun readBlockData(blockData: ByteArray, palette: Map<Int, BlockState>, length: Int, width: Int, blockDatas: Array<Array<Array<BlockData?>>>) {
            var index = 0

            var i = 0
            while (i < blockData.size) {
                var value = 0
                var varintLength = 0

                while (true) {
                    value = value or ((blockData[i].toInt() and 127) shl (varintLength++ * 7))
                    if (varintLength > 5) {
                        throw RuntimeException("VarInt too big (probably corrupted data)")
                    }

                    if ((blockData[i].toInt() and 128) != 128) {
                        ++i
                        val localPos = indexPos(
                            width,
                            length,
                            index
                        )
                        val data = BlockData()
                        data.blockState = palette[value]
                        blockDatas[localPos.x][localPos.y][localPos.z] = data
                        break
                    }
                    ++index
                }
            }
        }

        /**
         * Get the position of an index.
         * @param width The width of the schematic.
         * @param length The length of the schematic.
         * @param index The index.
         * @return The position of the index.
         */
        private fun indexPos(width: Int, length: Int, index: Int): BlockPos {
            val y = index / (width * length)
            val z = index % (width * length) / width
            val x = index % (width * length) % width
            return BlockPos(x, y, z)
        }

        /**
         * Read block entity data from a list tag.
         * @param blockEntities The list tag to read from.
         * @param blockDatas The array to store the read block entity data in.
         */
        private fun readBlockEntityData(blockEntities: ListTag, blockDatas: Array<Array<Array<BlockData?>>>) {
            for (i: Int in 0..<blockEntities.size) {
                val blockEntity = blockEntities.getCompound(i).copy()
                val posArr: IntArray = blockEntity.getIntArray("Pos")
                val pos = BlockPos(posArr[0], posArr[1], posArr[2])
                val blockData: BlockData = blockDatas[pos.x][pos.y][pos.z] ?: return
                if (blockData.hasBlockEntity) {
                    blockEntity.remove("Pos")
                    blockEntity.remove("Id")
                    blockData.blockEntityData = blockEntity
                }
            }
        }

        /**
         * Read entity data from a list tag.
         * @param entitiesTag The list tag to read from.
         * @param offset The offset to apply.
         * @return The read entity data.
         */
        private fun readEntityData(entitiesTag: ListTag?, offset: BlockPos): List<CompoundTag> {
            if (entitiesTag.isNullOrEmpty()) return arrayListOf()

            val entities: MutableList<CompoundTag> = mutableListOf()
            for (i: Int in 0..<entitiesTag.size) {
                val tag = entitiesTag.getCompound(i).copy()
                val id = tag.getString("Id")
                val posListTag: ListTag = tag.getList("Pos", CompoundTag.TAG_DOUBLE.toInt())
                val pos = Vec3(posListTag.getDouble(0), posListTag.getDouble(1), posListTag.getDouble(2))
                tag.remove("Id")
                tag.remove("Pos")
                tag.putString("id", id)
                val newPos = pos.subtract(offset.x.toDouble(), offset.y.toDouble(), offset.z.toDouble())
                tag.put("Pos", ListTag().also { list ->
                    list.add(DoubleTag.valueOf(newPos.x))
                    list.add(DoubleTag.valueOf(newPos.y))
                    list.add(DoubleTag.valueOf(newPos.z))
                })
                entities.add(tag)
            }

            return entities
        }

        /**
         * Check if a chunk contains a position.
         * @param chunkMinPos The minimum position of the chunk.
         * @param pos The position to check.
         * @return Whether the chunk contains the position.
         */
        private fun chunkContains(chunkMinPos: BlockPos, pos: BlockPos): Boolean {
            return chunkMinPos.x <= pos.x
                    && chunkMinPos.z <= pos.z
                    && chunkMinPos.x + 15 >= pos.x
                    && chunkMinPos.z + 15 >= pos.z
        }
    }

    /**
     * Place the schematic in a level at a position.
     * @param level The level to place the schematic in.
     * @param pos The position to place the schematic at.
     */
    fun place(level: ServerLevel, pos: BlockPos) {
        for (x: Int in 0..<this.width) {
            for (y: Int in 0..<this.height) {
                for (z: Int in 0..<this.length) {
                    val blockData: BlockData = this.blockDatas[x][y][z] ?: continue
                    val state = blockData.blockState ?: continue
                    val realPos = pos.offset(x, y, z)
                    level.setBlock(realPos, state, 2)
                }
            }
        }
    }

    /**
     * Place the schematic in a chunk.
     * @param chunk The chunk to place the schematic in.
     */
    fun placeChunk(chunk: ChunkAccess) {
        val minPos: BlockPos = chunk.pos.worldPosition
        val blockPos = MutableBlockPos()

        for (x: Int in 0..<this.width) {
            for (y: Int in 0..<this.height) {
                for (z: Int in 0..<this.length) {
                    val blockData = this.blockDatas[x][y][z] ?: continue
                    val blockState = blockData.blockState ?: continue
                    blockPos.set(x, y, z)
                    if (!chunkContains(minPos, blockPos)) continue
                    chunk.setBlockState(blockPos, blockState, false)
                    if (!blockData.hasBlockEntity) continue
                    val blockEntity = (blockState.block as EntityBlock).newBlockEntity(blockPos, blockState) ?: continue
                    blockEntity.load(blockData.blockEntityData!!)
                    chunk.setBlockEntity(blockEntity)
                }
            }
        }
    }

    /**
     * Spawn mobs in a world generation region.
     * @param level The world generation region to spawn mobs in.
     */
    @Suppress("DEPRECATION")
    fun spawnMobs(level: WorldGenRegion) {
        val mutable = MutableBlockPos()

        for (entityTag in this.entities) {
            val posTag = entityTag.getList("Pos", CompoundTag.TAG_DOUBLE.toInt())
            val pos = Vec3(posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2))
            mutable.set(pos.x, pos.y, pos.z)
            val chunkMinPos = level.center.worldPosition
            if (!chunkContains(chunkMinPos, mutable)) continue

            val entityId = entityTag.getString("id")
            EntityType.byString(entityId).getOrNull() ?: continue // continue if entity type is null
            val entity = EntityType.create(entityTag, level.level).getOrNull() ?: continue
            entity.moveTo(pos)
            level.addFreshEntityWithPassengers(entity)
        }
    }
}
