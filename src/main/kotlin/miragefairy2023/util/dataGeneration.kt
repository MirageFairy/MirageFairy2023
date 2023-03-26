@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonElement
import miragefairy2023.InitializationScope
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.predicate.StatePredicate

fun <T : Block> BlockScope<T>.generateBlockState(jsonElementSupplier: () -> JsonElement) {
    initializationScope.onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = item
            override fun get() = jsonElementSupplier()
        })
    }
}


inline fun <T> configure(receiver: T, block: T.() -> Unit) = receiver.apply(block)

fun <T : Block> BlockScope<T>.generateBlockLootTable(block: () -> LootTable.Builder) {
    initializationScope.onGenerateBlockLootTables {
        addDrop(item, block())
    }
}

fun <T : LootFunctionConsumingBuilder<T>> T.applyExplosionDecay(drop: ItemConvertible): T {
    return FabricBlockLootTableProvider.applyExplosionDecay(drop, this)!!
}

fun lootTable(block: (LootTable.Builder.() -> Unit)? = null): LootTable.Builder {
    return configure(LootTable.builder()!!) { block?.invoke(this) }
}

fun lootPool(block: (LootPool.Builder.() -> Unit)? = null): LootPool.Builder {
    return configure(LootPool.builder()!!) { block?.invoke(this) }
}

fun blockStatePropertyLootCondition(targetBlock: Block, block: (BlockStatePropertyLootCondition.Builder.() -> Unit)? = null): BlockStatePropertyLootCondition.Builder {
    return configure(BlockStatePropertyLootCondition.builder(targetBlock)!!) { block?.invoke(this) }
}

fun statePredicate(block: (StatePredicate.Builder.() -> Unit)? = null): StatePredicate.Builder {
    return configure(StatePredicate.Builder.create()!!) { block?.invoke(this) }
}

fun itemEntry(item: ItemConvertible, block: (LeafEntry.Builder<*>.() -> Unit)? = null): LeafEntry.Builder<*> {
    return configure(ItemEntry.builder(item)!!) { block?.invoke(this) }
}

fun alternativeEntry(vararg children: LootPoolEntry.Builder<*>, block: (AlternativeEntry.Builder.() -> Unit)? = null): AlternativeEntry.Builder {
    return configure(AlternativeEntry.builder(*children)!!) { block?.invoke(this) }
}

fun constantLootNumberProvider(value: Float) = ConstantLootNumberProvider.create(value)!!
fun uniformLootNumberProvider(min: Float, max: Float) = UniformLootNumberProvider.create(min, max)!!


fun InitializationScope.enJa(translationKey: String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey, en) }
    onGenerateJapaneseTranslations { it.add(translationKey, ja) }
}

fun InitializationScope.enJa(translationKey: () -> String, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(translationKey(), en) }
    onGenerateJapaneseTranslations { it.add(translationKey(), ja) }
}

fun InitializationScope.enJaItem(item: () -> Item, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(item(), en) }
    onGenerateJapaneseTranslations { it.add(item(), ja) }
}

fun InitializationScope.enJaItemGroup(itemGroup: () -> ItemGroup, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(itemGroup(), en) }
    onGenerateJapaneseTranslations { it.add(itemGroup(), ja) }
}
