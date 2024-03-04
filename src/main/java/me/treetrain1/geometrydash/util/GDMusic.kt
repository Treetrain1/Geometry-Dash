package me.treetrain1.geometrydash.util

import com.mojang.blaze3d.systems.RenderSystem
import de.keksuccino.melody.resources.audio.MelodyAudioException
import de.keksuccino.melody.resources.audio.SimpleAudioFactory.SourceType
import de.keksuccino.melody.resources.audio.openal.ALAudioBuffer
import de.keksuccino.melody.resources.audio.openal.ALAudioClip
import de.keksuccino.melody.resources.audio.openal.ALUtils
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import org.apache.commons.io.IOUtils
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import java.io.*
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

@Environment(EnvType.CLIENT)
object GDMusic {

    // TODO: add web
    @Throws(MelodyAudioException::class)
    fun mp3(audioSource: String, sourceType: SourceType): CompletableFuture<ALAudioClip> {
        RenderSystem.assertOnRenderThread()

        if (!ALUtils.isOpenAlReady()) {
            throw MelodyAudioException("Failed to create MP3 audio clip! OpenAL not ready! Audio source: $audioSource")
        }

        val clip: ALAudioClip = ALAudioClip.create() ?: throw MelodyAudioException("Failed to create MP3 audio clip! Clip was NULL for: $audioSource")
        when (sourceType) {
            SourceType.RESOURCE_LOCATION -> {
                val location: ResourceLocation? = ResourceLocation.tryParse(audioSource)
                if (location == null) {
                    clip.closeQuietly()
                    throw MelodyAudioException("Failed to create MP3 audio clip! ResourceLocation parsing failed: $audioSource")
                }

                val resource: Resource? = Minecraft.getInstance().getResourceManager().getResource(location).getOrNull()
                if (resource != null) {
                    try {
                        val input = resource.open()
                        val completableFuture: CompletableFuture<ALAudioClip> = CompletableFuture()
                        Thread {
                            val ex: Exception? = tryCreateAndSetMp3StaticBuffer(clip, input)
                            if (ex != null) {
                                completableFuture.completeExceptionally(ex)
                                clip.closeQuietly()
                            } else {
                                completableFuture.complete(clip)
                            }
                        }.start()
                        return completableFuture
                    } catch (ex: Exception) {
                        clip.closeQuietly()
                        throw MelodyAudioException("Failed to create MP3 audio clip! Failed to open ResourceLocation input stream: $audioSource").initCause(ex)
                    }
                } else {
                    clip.closeQuietly()
                    throw MelodyAudioException("Failed to create MP3 audio clip! Resource for ResourceLocation not found: $audioSource")
                }
            }
            SourceType.LOCAL_FILE -> {
                val file = File(audioSource)
                if (!file.isFile()) {
                    clip.closeQuietly()
                    throw MelodyAudioException("Failed to create MP3 audio clip! File not found: $audioSource")
                }
                try {
                    val input = FileInputStream(file)
                    val completableFuture: CompletableFuture<ALAudioClip> = CompletableFuture()
                    Thread {
                        val ex: Exception? = tryCreateAndSetMp3StaticBuffer(clip, input)
                        if (ex != null) {
                            completableFuture.completeExceptionally(ex)
                            clip.closeQuietly()
                        } else {
                            copmletableFuture.complete(clip)
                        }
                    }.start()
                    return completableFuture
                } catch (ex: Exception) {
                    clip.closeQuietly()
                    throw MelodyAudioException("Failed to create MP3 audio clip! Failed to open File input stream: $audioSource").initCause(ex)
                }
            }
            else -> {
                TODO()
            }
        }
    }

    fun tryCreateAndSetMp3StaticBuffer(setTo: ALAudioClip, `in`: InputStream): Exception? {
        val stream: AudioInputStream? = null
        val byteIn: ByteArrayInputStream? = null
        val exception: Exception? = null
        try {
            byteIn = ByteArrayInputStream(`in`.readAllBytes())
            stream = AudioSystem.getAudioInputStream(byteIn!!)
            val byteBuffer: ByteBuffer = ALUtils.readStreamIntoBuffer(stream!!)
            val format = stream!!.getFormat()
            val decodedFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                format.getSampleRate(),
                16,
                format.getChannels(),
                format.getChannels() * 2,
                format.getSampleRate(),
                false
            )
            val audioBuffer = AlAudioBuffer(byteBuffer, decodedFormat)
            setTo.setStaticBuffer(audioBuffer)
        } catch (ex: Exception) {
            exception = ex
        }
        IOUtils.closeQuietly(stream)
        IOUtils.closeQuietly(`in`)
        IOUtils.closeQuietly(byteIn)
        return exception
    }
}