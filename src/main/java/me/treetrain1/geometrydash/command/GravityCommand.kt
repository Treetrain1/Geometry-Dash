package me.treetrain1.geometrydash.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import me.treetrain1.geometrydash.util.gravity
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3


object GravityCommand {

    internal fun register(dispatcher: CommandDispatcher<CommandSourceStack>) = dispatcher.register(
        Commands.literal("gravity")
            .then(
                Commands.argument("gravityAmount", Vec3Argument.vec3(false))
                    .requires { source -> source.isPlayer && source.hasPermission(2) }
                    .executes { ctx -> set(ctx.source, Vec3Argument.getVec3(ctx, "gravityAmount"), listOf(ctx.source.playerOrException)) }
                    .then(
                        Commands.argument("targets", EntityArgument.entities())
                            .requires { source -> source.hasPermission(2) }
                            .executes { ctx -> set(ctx.source, Vec3Argument.getVec3(ctx, "gravityAmount"), EntityArgument.getEntities(ctx, "targets")) }
                    )
            )
    )

    private fun set(source: CommandSourceStack, gravity: Vec3, targets: Collection<Entity>): Int {
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
