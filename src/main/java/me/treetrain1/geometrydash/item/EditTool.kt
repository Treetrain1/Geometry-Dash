package me.treetrain1.geometrydash.item

import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.entity.Portal
import me.treetrain1.geometrydash.registry.RegisterEntities
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

/**
 * A tool for creating, editing, and deleting GD entities
 */
open class EditTool(props: Properties = Properties().stacksTo(1)) : Item(props) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)
        val tag = stack.getOrCreateTag()
        val type = tag.getType("type")
        val mode = tag.getMode("mode")

        // switch modes when shifting
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                val newTag = CompoundTag()
                // TODO: switch type from crouch + left click
                //newTag.putType("type", type.next())
                newTag.putMode("mode", mode.next())
                stack.setTag(newTag)
                return InteractionResultHolder(InteractionResult.SUCCESS, stack)
            }
        }

        when (mode) {
            Mode.CREATE -> {
                when (type) {
                    // TODO: implement
                    Type.SPIKE -> {}
                    Type.ORB -> {}
                    Type.PORTAL -> {
                        // create portal for now
                        val portal = Portal(RegisterEntities.PORTAL, level)
                        portal.type = Portal.PortalType.SHIP
                        portal.setPos(player.position())
                        if (level.addFreshEntity(portal)) {
                            return InteractionResultHolder.success(stack)
                        }
                    }
                    Type.CHECKPOINT -> {}
                }
            }
            else -> {} // TODO: implement
        }

        // copy of portal for now
        val portal = Portal(RegisterEntities.PORTAL, level)
        portal.type = Portal.PortalType.SHIP
        portal.setPos(player.position())
        if (level.addFreshEntity(portal)) {
            return InteractionResultHolder.success(stack)
        }
        return super.use(level, player, usedHand)
    }

    // the type of block/entity to operate with
    enum class Type : StringRepresentable {
        SPIKE,
        ORB,
        PORTAL
        CHECKPOINT;

        companion object {
            @JvmField
            val CODEC: EnumCodec<Type> = StringRepresentable.fromEnum(::values)

            fun CompoundTag.getType(key: String): Type {
                return CODEC.byName(key) ?: SPIKE
            }

            fun CompoundTag.putType(key: String, type: Type): CompoundTag {
                this.putString(key, type.serializedName)
                return this
            }
        }

        override fun next(): Type = when (this) {
            SPIKE -> ORB
            ORB -> PORTAL
            PORTAL -> CHECKPOINT
            CHECKPOINt -> SPIKE
        }

        override fun getSerializedName(): String = this.name.lowercase()
    }

    enum class Mode : StringRepresentable {
        CREATE, // create a new object
        EDIT, // edit object properties (ex: portal type)
        MOVE,
        COPY;

        companion object {
            @JvmField
            val CODEC: EnumCodec<Mode> = StringRepresentable.fromEnum(::values)

            fun CompoundTag.getMode(key: String): Mode {
                return CODEC.byName(key) ?: CREATE
            }

            fun CompoundTag.putMode(key: String, mode: Mode): CompoundTag {
                this.putString(key, mode.serializedName)
                return this
            }
        }

        fun next(): Mode = when (this) {
            CREATE -> EDIT
            EDIT -> MOVE
            MOVE -> COPY
            COPY -> CREATE
        }

        override fun getSerializedName(): String = this.name.lowercase()
    }
}