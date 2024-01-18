package me.treetrain1.geometrydash.command

import com.mojang.brigadier.context.CommandContext
import me.treetrain1.geometrydash.data.GDMode
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.StringRepresentableArgument
import net.minecraft.util.StringRepresentable

open class GDModeArgument private constructor() : StringRepresentableArgument<GDMode>(CODEC, ::modes) {
    companion object {
        private val CODEC = StringRepresentable.fromEnumWithMapping(::modes) { it.lowercase() }

        private fun modes(): Array<GDMode> = GDMode.entries.toTypedArray()

        fun gdMode(): GDModeArgument = GDModeArgument()

        fun getGDMode(ctx: CommandContext<CommandSourceStack>, arg: String) = ctx.getArgument(arg, GDMode::class.java)
    }

    override fun convertId(id: String): String = id.lowercase()
}
