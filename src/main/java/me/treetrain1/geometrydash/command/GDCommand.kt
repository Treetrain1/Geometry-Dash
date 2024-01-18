package me.treetrain1.geometrydash.command

import com.mojang.brigadier.CommandDispatcher
import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.data.GDMode
import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.network.GDModeSyncPacket
import me.treetrain1.geometrydash.util.log
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.StringRepresentableArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.GameRules


object GDCommand {

    internal fun register(dispatcher: CommandDispatcher<CommandSourceStack>) = dispatcher.register(
        Commands.literal("gd")
            .then(
                Commands.literal("toggle")
                    .requires { source -> source.isPlayer && source.hasPermission(2) }
                    .executes { ctx -> toggle(ctx.source, listOf(ctx.source.playerOrException)) }
            )
            .then(
                Commands.literal("set")
                    .requires { source -> source.isPlayer && source.hasPermission(2) }
                    .then(
                        Commands.argument("mode", GDModeArgument.gdMode())
                            .executes { ctx -> set(ctx.source, GDModeArgument.getGDMode(ctx, "mode"), listOf(ctx.source.playerOrException)) }
                            .then(
                                Commands.argument("targets", EntityArgument.players())
                                    .executes { ctx -> set(ctx.source, GDModeArgument.getGDMode(ctx, "mode"), EntityArgument.getPlayers(ctx, "targets")) }
                            )
                    )
            )
            .then(
                Commands.literal("exit")
                    .requires { source -> source.isPlayer }
                    .executes { ctx -> exit(ctx.source, listOf(ctx.source.playerOrException)) }
                    .then(
                        Commands.argument("targets", EntityArgument.players())
                            .requires { source -> source.hasPermission(2) }
                            .executes { ctx -> exit(ctx.source, EntityArgument.getPlayers(ctx, "targets"))}
                    )
            )
    )

    private fun toggle(source: CommandSourceStack, players: Collection<ServerPlayer>): Int {
        for (player in players) {
            val duck = player as PlayerDuck
            val dat: GDData = duck.`geometryDash$getGDData`()
            dat.toggleGD()
            val packet = GDModeSyncPacket(dat)
            ServerPlayNetworking.send(player, packet)
        }

        if (players.size == 1) {
            source.sendSuccess({ Component.translatable("commands.gd.toggle.success.single", players.first().displayName) }, true)
        } else {
            source.sendSuccess({ Component.translatable("commands.gd.toggle.success.multiple", players.size) }, true)
        }

        return players.size
    }

    private fun set(source: CommandSourceStack, mode: GDMode, players: Collection<ServerPlayer>): Int {
        var successful: Int = 0
        for (player in players) {
            val duck = player as PlayerDuck
            val dat: GDData = duck.`geometryDash$getGDData`()
            if (dat.setGD(mode)) {
                logSet(source, mode, player)
                val packet = GDModeSyncPacket(dat)
                ServerPlayNetworking.send(player, packet)
                successful++
            }
        }

        return successful
    }

    private fun logSet(source: CommandSourceStack, mode: GDMode, player: ServerPlayer) {
        val modeComponent = Component.literal(mode.title)
        if (source.entity === player) {
            source.sendSuccess({ Component.translatable("commands.gd.set.success.self", modeComponent) }, true)
        } else {
            if (source.level.gameRules.getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                player.sendSystemMessage(Component.translatable("gdMode.changed", modeComponent))
            }

            source.sendSuccess({ Component.translatable("commands.gd.set.success.other", player.displayName, modeComponent) }, true)
        }
    }

    private fun exit(source: CommandSourceStack, players: Collection<ServerPlayer>): Int {
        val packet = GDModeSyncPacket(null, 1.0)
        for (player in players) {
            (player as PlayerDuck).`geometryDash$getGDData`().exitGD()
            ServerPlayNetworking.send(player, packet)
        }

        if (players.size == 1) {
            source.sendSuccess({ Component.translatable("commands.gd.exit.success.single", players.first().name) }, true)
        } else {
            source.sendSuccess({ Component.translatable("commands.gd.exit.success.multiple", players.size) }, true)
        }

        return players.size
    }
}
