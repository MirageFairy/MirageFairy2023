@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

interface TagScope<T> {
    val feature: TagKey<T>
    fun onGenerate(block: FabricTagProvider<T>.FabricTagBuilder<T>.() -> Unit)
}

fun InitializationScope.itemTag(name: String, block: (FabricTagProvider<Item>.FabricTagBuilder<Item>.() -> Unit)? = null): TagScope<Item> {
    val tagKey = TagKey.of(Registry.ITEM_KEY, Identifier(this.modId, name))
    var builder: FabricTagProvider<Item>.FabricTagBuilder<Item>? = null
    val listeners = mutableListOf<(FabricTagProvider<Item>.FabricTagBuilder<Item>) -> Unit>()
    val tagScope = object : TagScope<Item> {
        override val feature get() = tagKey
        override fun onGenerate(block: FabricTagProvider<Item>.FabricTagBuilder<Item>.() -> Unit) {
            if (builder == null) {
                // ビルダー生成未完了
                listeners += block // 処理待ちに登録
            } else {
                // ビルダー生成済み
                block(builder!!) // その場で処理
            }
        }
    }
    onGenerateItemTags {

        // ビルダー生成
        builder = it(tagKey)

        // 待機中のリスナーを処理
        listeners.forEach {
            it(builder!!)
        }
        listeners.clear()

    }
    if (block != null) tagScope.onGenerate { block(this) }
    return tagScope
}

fun <T : Item> FeatureSlot<T>.registerToTag(tagScopeGetter: () -> TagScope<Item>) = initializationScope.onRegisterRecipes {
    tagScopeGetter().onGenerate {
        add(this@registerToTag.feature)
    }
}
