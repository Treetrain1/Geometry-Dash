package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag

data class SongSource(
    @JvmField val audioSource: String,
    @JvmField var startTimestamp: Float = 0F
) {

    companion object {
        @JvmField
        val CODEC: Codec<SongSource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("audio_source").forGetter(SongSource::audioSource),
                Codec.FLOAT.fieldOf("start_timestamp").forGetter(SongSource::startTimestamp),
            ).apply(instance, ::SongSource)
        }

        fun CompoundTag.getSongSource(key: String): SongSource? {
            if (!this.contains(key, CompoundTag.TAG_COMPOUND.toInt()))
                return null
            val song: CompoundTag = this.getCompound(key)
            val source = song.getString("audio_source")
            val timestamp = song.getFloat("start_timestamp")
            return SongSource(source, timestamp)
        }

        fun CompoundTag.putSongSource(key: String, source: SongSource?): CompoundTag {
            if (source == null) return this
            val comp = CompoundTag()
            comp.putString("audio_source", source.audioSource)
            comp.putFloat("start_timestamp", source.startTimestamp)
            this.put(key, comp)
            return this
        }
    }
}
