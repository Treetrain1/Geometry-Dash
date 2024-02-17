package me.treetrain1.geometrydash.worldgen

import net.minecraft.core.BlockPos
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
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.Vec3
import java.io.IOException

class BlockData(
    var blockState: BlockState? = null,
    var blockEntityData: CompoundTag? = null
) {
    inline val hasBlockState: Boolean get() = this.blockState != null
    inline val hasBlockEntity: Boolean get() = this.blockEntityData != null
}

// A Sponge V3 Schematic
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

        fun loadFromResource(level: ServerLevel, id: ResourceLocation): Schematic
            = loadFromResource(level.server, id)

        fun loadFromResource(server: MinecraftServer, id: ResourceLocation): Schematic {
            val resourceManager = server.resourceManager
            val resourceLocation = CONVERTER.idToFile(id)
            return load(resourceManager, resourceLocation)
        }

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

        private fun indexPos(width: Int, length: Int, index: Int): BlockPos {
            val y = index / (width * length)
            val z = index % (width * length) / width
            val x = index % (width * length) % width
            return BlockPos(x, y, z)
        }

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
    }
}
