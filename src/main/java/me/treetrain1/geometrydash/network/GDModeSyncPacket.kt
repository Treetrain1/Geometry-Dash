package me.treetrain1.geometrydash.network

import me.treetrain1.geometrydash.duck.PlayerDuck
import me.treetrain1.geometrydash.util.id
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer

data class GDModeSyncPacket(@JvmField val mode: GDMode?, @JvmField val scale: Double) : FabricPacket {
    companion object {
        @JvmField
        val PACKET_TYPE: PacketType<GDModeSyncPacket> = PacketType.create(id("gd_mode_sync"), ::GDModeSyncPacket)

        @Environment(EnvType.CLIENT)
        fun sendC2S() {
            val player = Minecraft.getInstance().player ?: return
            val duck = player as PlayerDuck

            ClientPlayNetworking.send(GDModeSyncPacket(duck.`geometryDash$isGDMode`()))
        }

        fun sendS2C(player: ServerPlayer) {
            val duck = player as PlayerDuck

            ServerPlayNetworking.send(player, GDModeSyncPacket(duck.`geometryDash$isGDMode`()))
        }

        inline fun sendS2C(players: Iterable<ServerPlayer>) = players.forEach { sendS2C(it) }
    }

    constructor(dat: GDData) : this(dat.mode, dat.scale)

    constructor(buf: FriendlyByteBuf) : this(buf.readDouble()) // TODO: do this properly

    override fun write(buf: FriendlyByteBuf) {
        buf.writeBoolean(this.mode) // TODO: do this properly
    }

    override fun getType(): PacketType<*> = PACKET_TYPE

}
