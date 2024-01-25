package me.treetrain1.geometrydash.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.network.GDModeSyncPacket
import me.treetrain1.geometrydash.util.gravity
import me.treetrain1.geometrydash.util.log
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.StringRepresentableArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.GameRules


object GravityCommand {

    internal fun register(dispatcher: CommandDispatcher<CommandSourceStack>) = dispatcher.register(
        Commands.literal("gravity")
            .then(
                Commands.argument("gravityAmount", DoubleArgumentType.doubleArg())
                    .requires { source -> source.isPlayer && source.hasPermission(2) }
                    .executes { ctx -> set(ctx.source, DoubleArgumentType.getDouble(ctx, "gravityAmount"), listOf(ctx.source.playerOrException)) }
                    .then(
                        Commands.argument("targets", EntityArgument.entities())
                            .requires { source -> source.hasPermission(2) }
                            .executes { ctx -> set(ctx.source, DoubleArgumentType.getDouble(ctx, "gravityAmount"), EntityArgument.getEntities(ctx, "targets")) }
                    )
            )
    )

    private fun set(source: CommandSourceStack, gravity: Double, targets: Collection<Entity>): Int {
        targets.forEach {
            it.gravity = gravity
        }

        if (targets.size == 1) {
            source.sendSuccess({ Component.translatable("commands.gravity.success.single", gravity, targets.first().name) }, true)
        } else {
            source.sendSuccess({ Component.translatable("commands.gravity.success.multiple", gravity, targets.size) }, true)
        }

        return targets.size
    }
}
