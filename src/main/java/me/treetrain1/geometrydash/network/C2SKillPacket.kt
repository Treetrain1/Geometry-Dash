package me.treetrain1.geometrydash.network

import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf

class C2SKillPacket() : FabricPacket {
    companion object {
        @JvmField
        val TYPE: PacketType<C2SKillPacket> = PacketType.create(id("kill"), ::C2SKillPacket)
    }

    constructor(buf: FriendlyByteBuf) : this()

    override fun write(buf: FriendlyByteBuf?) {}

    override fun getType(): PacketType<*> = TYPE
}
