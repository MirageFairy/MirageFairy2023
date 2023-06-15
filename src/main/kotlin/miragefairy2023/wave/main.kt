package miragefairy2023.wave

import java.io.File

fun main() {
    val bits = 8
    val windowSize = 1 shl bits
    val imageHeight = windowSize / 2 + 1
    val inputFile = File("""./src/main/resources/assets/miragefairy2023/sounds/random/magic4.png""")
    when (inputFile.extension) {
        "wav" -> {
            inputFile
                .readWaveform()
                .getSpectrogram(bits, 0.00025)
                .writeTo(inputFile.resolveSibling("${inputFile.nameWithoutExtension}.png"))
        }

        "png" -> {
            inputFile
                .readImage()
                .resize((48000 * 0.348852 + (windowSize - 1)).toInt(), imageHeight)
                .generatePhase()
                .also { it.writeTo(inputFile.resolveSibling("dump.png")) }
                .fromSpectrogram(bits, 0.00025)
                .toWav()
                .also { it.writeTo(inputFile.resolveSibling("dump.wav")) }
                .wavToOgg()
                .writeTo(inputFile.resolveSibling("${inputFile.nameWithoutExtension}.ogg"))
        }
    }
}
