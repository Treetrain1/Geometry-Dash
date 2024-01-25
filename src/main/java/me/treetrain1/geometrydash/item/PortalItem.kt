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

open class PortalItem(val type: Portal.PortalType = Portal.PortalType.CUBE, props: Properties = Properties().stacksTo(1)) : Item(props) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        val portal = Portal(RegisterEntities.PORTAL, level)
        portal.type = this.type
        portal.setPos(player.position())
        if (level.addFreshEntity(portal)) {
            return InteractionResultHolder.success(stack)
        }
        return super.use(level, player, usedHand)
    }
}
