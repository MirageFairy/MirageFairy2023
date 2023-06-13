package miragefairy2023.wave

import java.io.File

fun main() {
    val dir = File("""./build/wave""")
    val bits = 8
    val m = 0.00025

    val a = 1
    @Suppress("KotlinConstantConditions")
    when (a) {
        1 -> dir.resolve("test001.wav")
            .readWaveform()
            .getSpectrogram(bits, m)
            .writeTo(dir.resolve("test001.wav.png"))

        2 -> dir.resolve("test001.wav.png")
            .readImage()
            .generatePhase()
            .fromSpectrogram(bits, m)
            .writeTo(dir.resolve("test001.wav.png.wav"))

        3 -> dir.resolve("test001.wav.png.wav")
            .readWaveform()
            .getSpectrogram(bits, m)
            .writeTo(dir.resolve("test001.wav.png.wav.png"))
    }
}
