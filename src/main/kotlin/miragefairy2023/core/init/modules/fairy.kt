package miragefairy2023.core.init.modules

import miragefairy2023.core.init.module
import miragefairy2023.util.item
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import java.util.Optional

val fairyModule = module {

    // 妖精の共通アイテムモデル
    onGenerateItemModels {
        val layer0 = TextureKey.of("layer0")
        val layer1 = TextureKey.of("layer1")
        val layer2 = TextureKey.of("layer2")
        val layer3 = TextureKey.of("layer3")
        val layer4 = TextureKey.of("layer4")
        val model = Model(Optional.of(Identifier("minecraft", "item/generated")), Optional.empty(), layer0, layer1, layer2, layer3, layer4)
        model.upload(Identifier(modId, "item/fairy"), TextureMap().apply {
            put(layer0, Identifier(modId, "item/fairy_skin"))
            put(layer1, Identifier(modId, "item/fairy_back"))
            put(layer2, Identifier(modId, "item/fairy_front"))
            put(layer3, Identifier(modId, "item/fairy_hair"))
            put(layer4, Identifier(modId, "item/fairy_dress"))
        }, it.writer)
    }

    // 妖精登録
    item("air_fairy", { FairyItem(FabricItemSettings().group(ItemGroup.MATERIALS)) }) {
        onGenerateItemModels { it.register(item, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
        onRegisterColorProvider { it ->
            it(item) { _, tintIndex ->
                when (tintIndex) {
                    0 -> 0xFFCCCC
                    1 -> 0xFF6666
                    2 -> 0xFF6666
                    3 -> 0xFF4444
                    4 -> 0xAA0000
                    else -> 0xFFFFFF
                }
            }
        }
        onGenerateEnglishTranslations { it.add(item, "Airia") }
        onGenerateJapaneseTranslations { it.add(item, "空気精アイリャ") }
    }

}

class FairyItem(settings: Settings) : Item(settings) {

}
