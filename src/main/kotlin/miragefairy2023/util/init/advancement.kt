@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import miragefairy2023.util.Translation
import miragefairy2023.util.identifier
import miragefairy2023.util.text
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.predicate.item.ItemPredicate
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier

@Deprecated("TODO")
fun InitializationScope.advancement(
    name: String,
    displayItem: () -> ItemConvertible, // TODO -> Item
    enTitle: String,
    jaTitle: String,
    enDescription: String,
    jaDescription: String,

    @Suppress("UNUSED_PARAMETER")
    vararg dummy: Void,

    parent: (() -> Advancement)? = null,
    backgroundTexture: Identifier? = null,
    frame: AdvancementFrame = AdvancementFrame.TASK,

    initializer: Advancement.Builder.() -> Unit = {},
): () -> Advancement {
    lateinit var advancement: Advancement

    val title = Translation("advancements.$modId.$name.title", enTitle, jaTitle)
    val description = Translation("advancements.$modId.$name.description", enDescription, jaDescription)
    enJa(title)
    enJa(description)

    onGenerateAdvancements { consumer ->
        advancement = Advancement.Builder.create().apply {
            if (parent != null) parent(parent())
            display(displayItem(), text { title() }, text { description() }, backgroundTexture, frame, true, true, false)
            initializer(this)
        }.build(consumer, "$modId/$name")
    }

    return { advancement }
}

fun Advancement.Builder.criterion(item: Item) {
    this.criterion("got_${item.identifier.path}", InventoryChangedCriterion.Conditions.items(item))
}

fun Advancement.Builder.criterion(tagKey: TagKey<Item>) {
    this.criterion("got_${tagKey.id.path}", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(tagKey).build()))
}

fun Advancement.Builder.reward(lootTableId: Identifier) {
    this.rewards(AdvancementRewards.Builder.loot(lootTableId))
}
