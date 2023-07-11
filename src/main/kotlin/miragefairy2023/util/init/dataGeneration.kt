@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

import com.google.gson.JsonElement
import miragefairy2023.InitializationScope
import miragefairy2023.util.concat
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.string
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.block.Block
import net.minecraft.data.client.BlockStateSupplier
import net.minecraft.data.server.BlockLootTableGenerator
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.BlockStatePropertyLootCondition
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.GroupEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.entry.SequenceEntry
import net.minecraft.loot.function.LootFunctionConsumingBuilder
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.predicate.StatePredicate
import net.minecraft.state.property.Property
import net.minecraft.util.registry.Registry

fun <T : Block> InitializationScope.generateBlockState(block: T, jsonElementSupplier: () -> JsonElement) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = block
            override fun get() = jsonElementSupplier()
        })
    }
}

fun <T : Block> FeatureSlot<T>.generateBlockState(jsonElementSupplier: () -> JsonElement) {
    initializationScope.onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.blockStateCollector.accept(object : BlockStateSupplier {
            override fun getBlock() = feature
            override fun get() = jsonElementSupplier()
        })
    }
}

fun <T : Block> FeatureSlot<T>.generateHorizontalFacingBlockState() = generateBlockState {
    jsonObjectOf(
        "variants" to jsonObjectOf(listOf(
            "north" to 0,
            "south" to 180,
            "west" to 270,
            "east" to 90,
        ).map { (facing, y) ->
            "facing=$facing" to jsonObjectOf(
                "model" to "${"block/" concat id}".jsonPrimitive,
                "y" to y.jsonPrimitive,
            )
        }),
    )
}

fun <T : Block> InitializationScope.generateSimpleCubeAllBlockState(block: Block) {
    onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.registerSimpleCubeAll(block)
    }
}

fun <T : Block> FeatureSlot<T>.generateSimpleCubeAllBlockState() {
    initializationScope.onGenerateBlockStateModels { blockStateModelGenerator ->
        blockStateModelGenerator.registerSimpleCubeAll(feature)
    }
}


inline fun <T> configure(receiver: T, block: T.() -> Unit) = receiver.apply(block)

fun <T : Block> InitializationScope.generateDefaultBlockLootTable(block: T) = generateBlockLootTable(block) { BlockLootTableGenerator.drops(block) }

fun <T : Block> FeatureSlot<T>.generateDefaultBlockLootTable() = generateBlockLootTable { BlockLootTableGenerator.drops(feature) }

fun <T : Block> InitializationScope.generateBlockLootTable(block: T, block2: () -> LootTable.Builder) {
    onGenerateBlockLootTables {
        addDrop(block, block2())
    }
}

fun <T : Block> FeatureSlot<T>.generateBlockLootTable(block: () -> LootTable.Builder) {
    initializationScope.onGenerateBlockLootTables {
        addDrop(feature, block())
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

fun exactMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, value: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to value.jsonPrimitive,
            )
        ), null
    )
}

fun rangedMatchBlockStatePropertyLootCondition(block: Block, property: Property<Int>, min: Int, max: Int) = LootCondition.Builder {
    BlockStatePropertyLootCondition.Serializer().fromJson(
        jsonObjectOf(
            "block" to Registry.BLOCK.getId(block).string.jsonPrimitive,
            "condition" to "minecraft:block_state_property".jsonPrimitive,
            "properties" to jsonObjectOf(
                property.name to jsonObjectOf(
                    "min" to min.jsonPrimitive,
                    "max" to max.jsonPrimitive,
                ),
            )
        ), null
    )
}

fun statePredicate(block: (StatePredicate.Builder.() -> Unit)? = null): StatePredicate.Builder {
    return configure(StatePredicate.Builder.create()!!) { block?.invoke(this) }
}

fun itemLootPoolEntry(item: ItemConvertible, block: (LeafEntry.Builder<*>.() -> Unit)? = null): LeafEntry.Builder<*> {
    return configure(ItemEntry.builder(item)!!) { block?.invoke(this) }
}

fun alternativeLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (AlternativeEntry.Builder.() -> Unit)? = null): AlternativeEntry.Builder {
    return configure(AlternativeEntry.builder(*children)!!) { block?.invoke(this) }
}

fun groupLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (GroupEntry.Builder.() -> Unit)? = null): GroupEntry.Builder {
    return configure(GroupEntry.create(*children)!!) { block?.invoke(this) }
}

fun sequenceLootPoolEntry(vararg children: LootPoolEntry.Builder<*>, block: (SequenceEntry.Builder.() -> Unit)? = null): SequenceEntry.Builder {
    return configure(SequenceEntry.create(*children)!!) { block?.invoke(this) }
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

fun InitializationScope.enJaBlock(block: () -> Block, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(block(), en) }
    onGenerateJapaneseTranslations { it.add(block(), ja) }
}

fun InitializationScope.enJaItemGroup(itemGroup: () -> ItemGroup, en: String, ja: String) {
    onGenerateEnglishTranslations { it.add(itemGroup(), en) }
    onGenerateJapaneseTranslations { it.add(itemGroup(), ja) }
}
