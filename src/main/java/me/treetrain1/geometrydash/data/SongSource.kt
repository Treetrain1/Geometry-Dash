package me.treetrain1.geometrydash.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import de.keksuccino.melody.resources.audio.SimpleAudioFactory.SourceType

data class SongSource(
    @JvmField val audioSource: String,
    @JvmField val sourceType: SongSourceType,
) {

    companion object {
        @JvmField
        val CODEC: Codec<SongSource> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("audio_source").forGetter(SongSource::audioSource),
            )
        }
    }
}

enum class SongSourceType(val melodySource: SourceType) : StringRepresentable {
    RESOURCE_LOCATION(SourceType.RESOURCE_LOCATION),
    LOCAL_FILE(SourceType.LOCAL_FILE),
    WEB(SourceType.WEB),
} {

    companion object {
        @JvmField
        val CODEC: Codec<SongSourceType> = StringRepresentable.fromEnum(::values)

        fun CompoundTag.getSongSourceType(key: String): SongSource? {
            if (!this.contains(key, CompoundTag.TAG_STRING.toInt()))
                return null
            val str: String = this.getString(key)
            return try {
                SongSource.valueOf(str.uppercase())
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

    fun toMelody(): SourceType = this.melody
}