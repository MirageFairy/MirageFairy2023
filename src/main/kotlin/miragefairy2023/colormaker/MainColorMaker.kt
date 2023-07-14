package miragefairy2023.colormaker

import miragefairy2023.MirageFairy2023
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun loadImage(name: String): BufferedImage = ImageIO.read(File("src/main/resources/assets/${MirageFairy2023.modId}/textures/item/$name.png"))

object MainColorMaker {
    @JvmStatic
    fun main(args: Array<String>) {
        WindowColorMaker(
            { loadImage(it) },
            listOf(
                layeredImageSettingOf(
                    4,
                    LayerSetting("fairy_layer0", ColorExpression("@skin")),
                    LayerSetting("fairy_layer1", ColorExpression("#00BE00")),
                    LayerSetting("fairy_layer2", ColorExpression("@darker")),
                    LayerSetting("fairy_layer3", ColorExpression("@brighter")),
                    LayerSetting("fairy_layer4", ColorExpression("@hair"))
                ),
                layeredImageSettingOf(
                    4,
                    LayerSetting("mirage_wisp_layer0", ColorExpression("@darker")),
                    LayerSetting("mirage_wisp_layer1", ColorExpression("@skin")),
                    LayerSetting("mirage_wisp_layer2", ColorExpression("@brighter")),
                    LayerSetting("mirage_wisp_layer3", ColorExpression("@hair"))
                ),
                layeredImageSettingOf(
                    4,
                    LayerSetting("sphere_layer0", ColorExpression("@darker")),
                    LayerSetting("sphere_layer1", ColorExpression("@hair")),
                    LayerSetting("sphere_layer2", ColorExpression("@skin")),
                    LayerSetting("sphere_layer3", ColorExpression("@brighter"))
                ),
            ),
            listOf("skin", "darker", "brighter", "hair")
        ).isVisible = true
    }
}
