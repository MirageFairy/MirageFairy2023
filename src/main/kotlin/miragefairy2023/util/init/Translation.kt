@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.Translation
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

fun InitializationScope.enJa(translationKey: String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey, en) }
    onGenerateJapaneseTranslations { it.add(translationKey, ja) }
}

fun InitializationScope.enJa(translationKey: () -> String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey(), en) }
    onGenerateJapaneseTranslations { it.add(translationKey(), ja) }
}

fun InitializationScope.enJa(item: Item, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(item, en) }
    onGenerateJapaneseTranslations { it.add(item, ja) }
}

fun InitializationScope.enJa(block: Block, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(block, en) }
    onGenerateJapaneseTranslations { it.add(block, ja) }
}

fun InitializationScope.enJa(itemGroup: ItemGroup, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(itemGroup, en) }
    onGenerateJapaneseTranslations { it.add(itemGroup, ja) }
}

fun InitializationScope.enJa(translation: Translation) = enJa(translation.key, translation.en, translation.ja)
