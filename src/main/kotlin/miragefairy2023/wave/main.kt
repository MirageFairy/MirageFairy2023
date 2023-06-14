package miragefairy2023.wave

import java.io.File

fun main() {
    val inputFile = File("""./src/main/resources/assets/miragefairy2023/sounds/random/magic1.png""")
    when (inputFile.extension) {
        "wav" -> {
            inputFile
                .readWaveform()
                .getSpectrogram(8, 0.00025)
                .writeTo(inputFile.resolveSibling("${inputFile.nameWithoutExtension}.png"))
        }

        "png" -> {
            inputFile
                .readImage()
                .generatePhase()
                .fromSpectrogram(8, 0.00025)
                .toWav()
                .wavToOgg()
                .writeTo(inputFile.resolveSibling("${inputFile.nameWithoutExtension}.ogg"))
        }
    }
}
