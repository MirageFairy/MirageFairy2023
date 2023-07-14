package miragefairy2023.colormaker

object MainColorMakerFairy {
    @JvmStatic
    fun main(args: Array<String>) {
        WindowColorMaker(
            { loadImage(it) },
            listOf(
                layeredImageSettingOf(
                    LayerSetting("fairy_skin", ColorExpression("@skin")),
                    LayerSetting("fairy_dress", ColorExpression("@dress")),
                    LayerSetting("fairy_back", ColorExpression("@back")),
                    LayerSetting("fairy_front", ColorExpression("@front")),
                    LayerSetting("fairy_hair", ColorExpression("@hair"))
                )
            ),
            listOf("dress", "skin", "front", "back", "hair")
        ).isVisible = true
    }
}
