package me.treetrain1.geometrydash.util

import com.mojang.blaze3d.systems.RenderSystem
import de.keksuccino.melody.resources.audio.MelodyAudioException
import de.keksuccino.melody.resources.audio.SimpleAudioFactory.SourceType
import de.keksuccino.melody.resources.audio.openal.ALAudioBuffer
import de.keksuccino.melody.resources.audio.openal.ALAudioClip
import de.keksuccino.melody.resources.audio.openal.ALUtils
import javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader
import me.treetrain1.geometrydash.util.GDMusic.ConsumingSupplier
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import org.apache.commons.io.IOUtils
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioFormat.Encoding
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.jvm.optionals.getOrNull


@Environment(EnvType.CLIENT)
object GDMusic {

    private val BASIC_URL_TEXT_VALIDATOR: ConsumingSupplier<String, Boolean> = ConsumingSupplier { consumes ->
        if (consumes != null && consumes.replace(" ", "").isNotEmpty()) {
            if ((consumes.startsWith("http://") || consumes.startsWith("https://")) && consumes.contains("."))
                return@ConsumingSupplier true
        }
        return@ConsumingSupplier false
    }

    fun mp3Clip(audioSource: String, sourceType: SourceType): ALAudioClip? {
        val futureMp3 = mp3(audioSource, sourceType)
        return try {
            futureMp3.join()
        } catch (e: Exception) {
            null
        }
    }

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
                        Thread() {
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
                    Thread() {
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
                    throw MelodyAudioException("Failed to create MP3 audio clip! Failed to open File input stream: $audioSource").initCause(ex)
                }
            }
            else -> {
                if (!BASIC_URL_TEXT_VALIDATOR.get(audioSource)) {
                    clip.closeQuietly()
                    throw MelodyAudioException("Failed to create MP3 audio clip! Invalid URL: $audioSource")
                }
                try {
                    val webIn = openWebResourceStream(audioSource)
                    val completableFuture: CompletableFuture<ALAudioClip> = CompletableFuture()
                    Thread {
                        val ex: Exception? = tryCreateAndSetMp3StaticBuffer(clip, webIn)
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
                    throw MelodyAudioException("Failed to create MP3 audio clip! Failed to open web input stream: $audioSource").initCause(ex)
                }
            }
        }
    }

    fun tryCreateAndSetMp3StaticBuffer(setTo: ALAudioClip, `in`: InputStream): Exception? {
        var stream: AudioInputStream? = null
        var decodedStream: AudioInputStream? = null
        var byteIn: ByteArrayInputStream? = null
        var exception: Exception? = null
        try {
            byteIn = ByteArrayInputStream(`in`.readAllBytes())
            stream = MpegAudioFileReader().getAudioInputStream(byteIn)
            val format = stream.format
            val decodedFormat = AudioFormat( // PCM
                format.sampleRate,
                16,
                format.channels,
                true,
                false
            )
            decodedStream = AudioSystem.getAudioInputStream(decodedFormat, stream)
            val byteBuffer: ByteBuffer = ALUtils.readStreamIntoBuffer(decodedStream!!)
            val audioBuffer = ALAudioBuffer(byteBuffer, decodedStream.format)
            setTo.setStaticBuffer(audioBuffer)
        } catch (ex: Exception) {
            exception = ex
        }
        IOUtils.closeQuietly(stream)
        IOUtils.closeQuietly(decodedStream)
        IOUtils.closeQuietly(`in`)
        IOUtils.closeQuietly(byteIn)
        return exception
    }

    @Throws(IOException::class)
    private fun openWebResourceStream(resourceURL: String): InputStream {
        val actualURL = URL(resourceURL)
        val connection = actualURL.openConnection()
        connection.addRequestProperty("User-Agent", "Mozilla/4.0")
        return connection.inputStream
    }

    private fun interface ConsumingSupplier<C, R> {
        fun get(consumes: C): R
    }
}