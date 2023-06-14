package miragefairy2023.wave

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object WaveKt

private val logger: Logger = LoggerFactory.getLogger(WaveKt::class.java)

fun File.readWaveform(): DoubleArray {
    AudioSystem.getAudioInputStream(this).use { input ->
        logger.info("${input.format}")
        logger.info("${input.format.sampleRate}")
        logger.info("${input.format.frameSize}")

        val inputBytes = input.readAllBytes()
        val length = inputBytes.size / input.format.frameSize
        val outputDoubles = DoubleArray(length)
        repeat(length) { i ->

            val a = i * input.format.frameSize
            var value = 0.0
            repeat(input.format.channels) { channel ->
                value += when (input.format.sampleSizeInBits) {
                    8 -> inputBytes[a + 1 * channel + 0].toInt().toDouble()

                    16 -> {
                        val b0 = inputBytes[a + 2 * channel + 0]
                        val b1 = inputBytes[a + 2 * channel + 1]
                        ((b0.toInt() and 0xFF) or (b1.toInt() shl 8)).toDouble()
                    }

                    else -> throw RuntimeException("Bits not supported: ${input.format.sampleSizeInBits}")
                }
            }
            value /= input.format.channels

            outputDoubles[i] = value
        }

        return outputDoubles
    }
}

fun File.writeWaveform(waveform: DoubleArray) {

    val bytes = ByteArray(waveform.size * 2)
    repeat(waveform.size) { i ->
        val int = waveform[i].toInt().coerceIn(-32768 until 32768)
        bytes[2 * i + 0] = (int shr 0 and 0xFF).toByte()
        bytes[2 * i + 1] = (int shr 8 and 0xFF).toByte()
    }

    val format = AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        48000.0F,
        16,
        1,
        2,
        48000.0F,
        false,
    )
    AudioSystem.write(AudioInputStream(ByteArrayInputStream(bytes), format, waveform.size.toLong()), AudioFileFormat.Type.WAVE, this)
}

fun DoubleArray.writeTo(file: File) = file.writeWaveform(this)

fun ByteArray.writeTo(file: File) = file.writeBytes(this)

fun DoubleArray.toWav(): ByteArray {

    val bytes = ByteArray(this.size * 2)
    repeat(this.size) { i ->
        val int = this[i].toInt().coerceIn(-32768 until 32768)
        bytes[2 * i + 0] = (int shr 0 and 0xFF).toByte()
        bytes[2 * i + 1] = (int shr 8 and 0xFF).toByte()
    }

    val output = ByteArrayOutputStream()

    val format = AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        48000.0F,
        16,
        1,
        2,
        48000.0F,
        false,
    )
    AudioSystem.write(AudioInputStream(ByteArrayInputStream(bytes), format, this.size.toLong()), AudioFileFormat.Type.WAVE, output)

    return output.toByteArray()
}

fun ByteArray.wavToOgg(): ByteArray {
    val processBuilder = ProcessBuilder("bash", "-c", "ffmpeg -i - -f ogg -")
    val process = processBuilder.start()
    return runBlocking {
        launch(Dispatchers.IO) {
            process.outputStream.use { output ->
                output.write(this@wavToOgg)
            }
        }
        val err = async(Dispatchers.IO) {
            process.errorStream.use { input ->
                input.readBytes()
            }
        }
        val result = async(Dispatchers.IO) {
            process.inputStream.use { input ->
                input.readBytes()
            }
        }
        val returnCode = process.waitFor()
        if (returnCode != 0) throw IOException("Process exit: $returnCode\n${err.await().toString(Charsets.UTF_8).replace("""\n+\Z""".toRegex(), "")}")
        result.await()
    }
}


fun Array<Complex>.fft(): Array<Complex> {
    val fft = FFT(false)
    fft.data = this
    fft.execute()
    return fft.data
}

fun Array<Complex>.ifft(): Array<Complex> {
    val fft = FFT(true)
    fft.data = this
    fft.execute()
    return fft.data
}


fun hanningWindow(t: Double, length: Double) = 0.5 - 0.5 * cos(2 * PI * t / length)

fun DoubleArray.getSpectrogram(bits: Int, m: Double): BufferedImage {
    check(bits >= 4)
    val windowSize = 1 shl bits
    logger.info("Window Size: $windowSize")
    logger.info("Input Waveform Length: ${this.size}")
    val width = this.size - windowSize + 1
    check(width >= 1)
    val height = windowSize / 2 + 1

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    repeat(width) { x ->

        val complexWindowedSubWaveform = this.sliceArray(x until x + windowSize).mapIndexed { i, it ->
            val w = hanningWindow(i.toDouble(), windowSize.toDouble())
            Complex(it * w, 0.0)
        }.toTypedArray()
        val outputSpectrum = complexWindowedSubWaveform.fft()

        repeat(height) { y ->
            val r = outputSpectrum[y].re * m
            val g = -128.0 + outputSpectrum[y].abs() * m
            val b = outputSpectrum[y].im * m
            val rgb = (r.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 16) or
                (g.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 8) or
                (b.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 0)
            image.setRGB(x, height - 1 - y, rgb)
        }

    }

    return image
}

fun BufferedImage.fromSpectrogram(bits: Int, m: Double): DoubleArray {
    check(bits >= 4) // 8
    val windowSize = 1 shl bits // 256
    check(width >= windowSize)
    check(height == windowSize / 2 + 1) // 129

    val waveforms = mutableListOf<DoubleArray>()
    val zero = Complex(0.0, 0.0)
    repeat(width) { x -> // 0 .. width - 1

        val spectrum = Array(windowSize) { zero } // Complex[256]
        repeat(height) { y -> // 0 .. 128
            val rgb = this.getRGB(x, height - 1 - y) // (y = 128 .. 0)
            val r = ((rgb shr 16 and 0xFF) - 128).toDouble() / m
            //val g = ((rgb shr 8 and 0xFF) - 128).toDouble() / m
            val b = ((rgb shr 0 and 0xFF) - 128).toDouble() / m

            spectrum[y] = Complex(r, b) // [index = 0 .. 128]
            if (y != 0 && y != height - 1) spectrum[windowSize - y] = Complex(r, -b) // [index = 256 .. 128]
            // y = 0 の画素は直流分を表すため、1か所にしかマッピングされない
            // 虚軸はインデックスが逆になると符号が入れ替わり、実軸は同じ値になる
            // y = 0, 128 において、必ず b = 0 になる性質がある
            // y = 128 のとき、普通には complexes[128] に2度代入することになるが、 b = 0 であるため等価となる
        }

        waveforms += spectrum.ifft().map { it.re }.toDoubleArray()
    }

    val length = waveforms.size - windowSize + 1 // width - 255
    val waveform = DoubleArray(length)
    repeat(length) { x ->
        var value = 0.0
        repeat(windowSize) { i -> // 0 .. 255
            val w = hanningWindow(i.toDouble(), windowSize.toDouble())
            value += waveforms[x + i][windowSize - 1 - i] * w
        }
        waveform[x] = value / windowSize * 2
    }

    return waveform
}

fun BufferedImage.generatePhase(): BufferedImage {
    val basePhase = (0 until height).map { 2 * PI * Math.random() }
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    repeat(width) { x ->
        repeat(height) { imageY -> // height = 129, imageY = 0 .. 128
            val y = height - 1 - imageY // 128 .. 0

            // y = 0 のとき、 w = 0
            // y = 1 のとき、 w = 2PI / 256
            // y = 32 のとき、 w = 2PI / 8
            // y = 64 のとき、 w = 2PI / 4
            // y = 128 のとき、 w = 2PI / 2
            val w = if (y != 0) 2.0 * PI / 256.0 * y else 0.0

            val inputRgb = getRGB(x, imageY)
            val g = (inputRgb shr 8 and 0xFF).toDouble()

            val r = g * cos(basePhase[y] + w * x)
            val b = g * sin(basePhase[y] + w * x)
            val outputRgb = (r.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 16) or
                (g.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 8) or
                (b.toInt().coerceIn(-128, 127) + 128 and 0xFF shl 0)

            image.setRGB(x, imageY, outputRgb)
        }
    }
    return image
}


fun File.readImage(): BufferedImage = ImageIO.read(this)

fun File.writeImage(image: BufferedImage) {
    ImageIO.write(image, "png", this)
}

fun BufferedImage.writeTo(file: File) = file.writeImage(this)
