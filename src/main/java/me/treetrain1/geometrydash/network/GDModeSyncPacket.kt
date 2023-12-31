package me.treetrain1.geometrydash.network

import me.treetrain1.geometrydash.data.GDData
import me.treetrain1.geometrydash.data.GDMode
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

            ClientPlayNetworking.send(GDModeSyncPacket(duck.`geometryDash$getGDData`()))
        }

        fun sendS2C(player: ServerPlayer) {
            val duck = player as PlayerDuck

            ServerPlayNetworking.send(player, GDModeSyncPacket(duck.`geometryDash$getGDData`()))
        }

        inline fun sendS2C(players: Iterable<ServerPlayer>) = players.forEach { sendS2C(it) }
    }

    constructor(dat: GDData) : this(dat.mode, dat.scale)

    constructor(buf: FriendlyByteBuf) : this(buf.readNullable { buf1 -> buf1.readInt() }?.let { GDMode.entries[it] }, buf.readDouble())

    override fun write(buf: FriendlyByteBuf) {
        buf.writeNullable(this.mode?.ordinal) { buf1, ord -> buf1.writeInt(ord) }

        buf.writeDouble(this.scale) // TODO: do this properly
    }

    override fun getType(): PacketType<*> = PACKET_TYPE

}
