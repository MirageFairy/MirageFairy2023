@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import miragefairy2023.InitializationScope
import miragefairy2023.util.Translation
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

fun InitializationScope.enJa(translationKey: String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey, en) }
    onGenerateJapaneseTranslations { it.add(translationKey, ja) }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.enJa(translationKey: () -> String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey(), en) }
    onGenerateJapaneseTranslations { it.add(translationKey(), ja) }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.enJaItem(item: () -> Item, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(item(), en) }
    onGenerateJapaneseTranslations { it.add(item(), ja) }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.enJaBlock(block: () -> Block, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(block(), en) }
    onGenerateJapaneseTranslations { it.add(block(), ja) }
}

@Deprecated("Removing") // TODO remove
fun InitializationScope.enJaItemGroup(itemGroup: () -> ItemGroup, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(itemGroup(), en) }
    onGenerateJapaneseTranslations { it.add(itemGroup(), ja) }
}

fun InitializationScope.translation(translation: Translation) = enJa(translation.key, translation.en, translation.ja)
