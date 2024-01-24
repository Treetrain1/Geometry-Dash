package me.treetrain1.geometrydash.network

import me.treetrain1.geometrydash.util.id
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf

class C2SFailPacket() : FabricPacket {
    companion object {
        @JvmField
        val TYPE: PacketType<C2SFailPacket> = PacketType.create(id("kill"), ::C2SFailPacket)
    }

    constructor(buf: FriendlyByteBuf) : this()

    override fun write(buf: FriendlyByteBuf?) {}

    override fun getType(): PacketType<*> = TYPE
}
