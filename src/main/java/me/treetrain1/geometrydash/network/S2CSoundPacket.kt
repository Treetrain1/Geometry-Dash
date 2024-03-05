package me.treetrain1.geometrydash.network

import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf

class S2CSoundPacket() : FabricPacket {
    companion object {
        @JvmField
        val TYPE: PacketType<S2CSoundPacket> = PacketType.create(id("sound"), ::S2CSoundPacket)
    }

    constructor(buf: FriendlyByteBuf) : this()

    override fun write(buf: FriendlyByteBuf?) {}

    override fun getType(): PacketType<*> = TYPE
}
