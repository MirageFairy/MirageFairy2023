package miragefairy2023

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import miragefairy2023.util.double
import miragefairy2023.util.get
import miragefairy2023.util.init.Translation
import miragefairy2023.util.string
import miragefairy2023.util.wrapper
import net.minecraft.util.Identifier

class MirageFairy2023ReiServerPlugin : REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(Categories.FAIRY_METAMORPHOSIS_ALTAR, FairyMetamorphosisAltarDisplay.serializer())
        registry.register(Categories.BLOCK_FAIRY_RELATION, BlockFairyRelationDisplay.serializer())
        registry.register(Categories.ENTITY_TYPE_FAIRY_RELATION, EntityTypeFairyRelationDisplay.serializer())
    }
}


object Categories {
    val FAIRY_METAMORPHOSIS_ALTAR: CategoryIdentifier<FairyMetamorphosisAltarDisplay> = CategoryIdentifier.of(MirageFairy2023.modId, "plugins/fairy_metamorphosis_altar")
    val BLOCK_FAIRY_RELATION: CategoryIdentifier<BlockFairyRelationDisplay> = CategoryIdentifier.of(MirageFairy2023.modId, "plugins/block_fairy_relation")
    val ENTITY_TYPE_FAIRY_RELATION: CategoryIdentifier<EntityTypeFairyRelationDisplay> = CategoryIdentifier.of(MirageFairy2023.modId, "plugins/entity_type_fairy_relation")
}

val BLOCK_FAIRY_RELATION_KEY = Translation("category.miragefairy2023.block_fairy_relation", "Block Fairy Relation", "ブロック妖精連携")
val ENTITY_TYPE_FAIRY_RELATION_KEY = Translation("category.miragefairy2023.entity_type_fairy_relation", "Entity Type Fairy Relation", "エンティティタイプ妖精連携")

class FairyMetamorphosisAltarDisplay(
    input: EntryIngredient,
    val rate: Double,
    output: EntryIngredient,
) : BasicDisplay(listOf(input), listOf(output)) {
    companion object {
        fun serializer(): Serializer<FairyMetamorphosisAltarDisplay> = Serializer.of(
            { inputs, outputs, _, tag -> FairyMetamorphosisAltarDisplay(inputs[0], tag.wrapper["rate"].double.get() ?: 0.0, outputs[0]) },
            { display, tag -> tag.wrapper["rate"].double.set(display.rate) },
        )
    }

    override fun getCategoryIdentifier() = Categories.FAIRY_METAMORPHOSIS_ALTAR
}

class BlockFairyRelationDisplay(
    input: EntryIngredient,
    output: EntryIngredient,
    val motif: Identifier,
) : BasicDisplay(listOf(input), listOf(output)) {
    companion object {
        fun serializer(): Serializer<BlockFairyRelationDisplay> = Serializer.of(
            { inputs, outputs, _, tag -> BlockFairyRelationDisplay(inputs[0], outputs[0], Identifier(tag.wrapper["motif"].string.get())) },
            { display, tag -> tag.wrapper["motif"].string.set(display.motif.string) },
        )
    }

    override fun getCategoryIdentifier() = Categories.BLOCK_FAIRY_RELATION
}

class EntityTypeFairyRelationDisplay(
    input: EntryIngredient,
    output: EntryIngredient,
    val motif: Identifier,
) : BasicDisplay(listOf(input), listOf(output)) {
    companion object {
        fun serializer(): Serializer<EntityTypeFairyRelationDisplay> = Serializer.of(
            { inputs, outputs, _, tag -> EntityTypeFairyRelationDisplay(inputs[0], outputs[0], Identifier(tag.wrapper["motif"].string.get())) },
            { display, tag -> tag.wrapper["motif"].string.set(display.motif.string) },
        )
    }

    override fun getCategoryIdentifier() = Categories.ENTITY_TYPE_FAIRY_RELATION
}
