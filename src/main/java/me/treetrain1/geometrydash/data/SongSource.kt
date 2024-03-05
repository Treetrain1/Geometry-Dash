package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import de.keksuccino.melody.resources.audio.SimpleAudioFactory.SourceType
import me.treetrain1.geometrydash.data.SongSourceType.Companion.getSongSourceType
import me.treetrain1.geometrydash.data.SongSourceType.Companion.putSongSourceType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.StringRepresentable

data class SongSource(
    @JvmField val audioSource: String,
    @JvmField val sourceType: SongSourceType,
) {

    companion object {
        @JvmField
        val CODEC: Codec<SongSource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("audio_source").forGetter(SongSource::audioSource),
                SongSourceType.CODEC.fieldOf("source_type").forGetter(SongSource::sourceType)
            ).apply(instance, ::SongSource)
        }

        fun CompoundTag.getSongSource(key: String): SongSource? {
            if (!this.contains(key, CompoundTag.TAG_COMPOUND.toInt()))
                return null
            val song: CompoundTag = this.getCompound(key)
            val source = song.getString("audio_source")
            val sourceType = song.getSongSourceType("source_type") ?: return null
            return SongSource(source, sourceType)
        }

        fun CompoundTag.putSongSource(key: String, source: SongSource?): CompoundTag {
            if (source == null) return this
            val comp = CompoundTag()
            comp.putString("audio_source", source.audioSource)
            comp.putSongSourceType("source_type", source.sourceType)
            this.put(key, comp)
            return this
        }
    }
}

enum class SongSourceType(val melodySource: SourceType) : StringRepresentable {
    RESOURCE_LOCATION(SourceType.RESOURCE_LOCATION),
    LOCAL_FILE(SourceType.LOCAL_FILE),
    WEB(SourceType.WEB_FILE);

    companion object {
        @JvmField
        val CODEC: Codec<SongSourceType> = StringRepresentable.fromEnum(::values)

        fun CompoundTag.getSongSourceType(key: String): SongSourceType? {
            if (!this.contains(key, CompoundTag.TAG_STRING.toInt()))
                return null
            val str: String = this.getString(key)
            return try {
                SongSourceType.valueOf(str.uppercase())
            } catch (e: Exception) {
                null
            }
        }

        fun CompoundTag.putSongSourceType(key: String, type: SongSourceType?): CompoundTag {
            this.putString(key, type?.serializedName ?: "")
            return this
        }
    }

    override fun getSerializedName(): String
        = this.name.lowercase()

    fun toMelody(): SourceType = this.melodySource
}
