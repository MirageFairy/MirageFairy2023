package miragefairy2023.modules.fairy

import miragefairy2023.InitializationScope
import miragefairy2023.modules.CommonFairyEntry
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.MirageFlourItem
import miragefairy2023.modules.passiveskill.BiomePassiveSkillCondition
import miragefairy2023.util.concat
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.group
import miragefairy2023.util.text
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

class FairyRecipes {
    val recipes = mutableListOf<FairyRecipe>()
}

interface FairyRecipe {
    fun getWikiString(): String
    fun init(initializationScope: InitializationScope, fairyCard: FairyCard)
}

fun FairyRecipes.always() = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "コモン：全世界"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { true }
        }
    }
}

fun FairyRecipes.overworld() = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "コモン：地上世界"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.dimension.natural }
        }
    }
}

fun FairyRecipes.biome(biomeTag: TagKey<Biome>) = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "コモン：${text { translate(biomeTag.id.toTranslationKey(BiomePassiveSkillCondition.keyPrefix)) }.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.getBiome(it.blockPos).isIn(biomeTag) }
        }
    }
}

fun FairyRecipes.biome(biome: RegistryKey<Biome>) = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "コモン：${text { translate(biome.value.toTranslationKey("biome")) }.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            MirageFlourItem.COMMON_FAIRY_LIST += CommonFairyEntry(fairyCard.fairy) { it.world.getBiome(it.blockPos) === biome }
        }
    }
}

fun FairyRecipes.block(block: Block) = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "夢：ブロック：${block.name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            BLOCK_FAIRY_RELATION_LIST += BlockFairyRelation(block, fairyCard.fairy)
        }
    }
}

fun FairyRecipes.entityType(entityType: EntityType<*>) = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "夢：エンティティ：${entityType.name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            ENTITY_TYPE_FAIRY_RELATION_LIST += EntityTypeFairyRelation(entityType, fairyCard.fairy)
        }
    }
}

fun FairyRecipes.recipe(inputItem: Item) = this.also {
    this.recipes += object : FairyRecipe {
        override fun getWikiString() = "クラフト：${inputItem.name.string}"
        override fun init(initializationScope: InitializationScope, fairyCard: FairyCard) {
            initializationScope.onGenerateRecipes {
                val mirageFlourItem = when (fairyCard.rare) {
                    0 -> DemonItemCard.TINY_MIRAGE_FLOUR.item
                    1, 2 -> DemonItemCard.MIRAGE_FLOUR.item
                    3, 4 -> DemonItemCard.RARE_MIRAGE_FLOUR.item
                    5, 6 -> DemonItemCard.VERY_RARE_MIRAGE_FLOUR.item
                    7, 8 -> DemonItemCard.ULTRA_RARE_MIRAGE_FLOUR.item
                    9, 10 -> DemonItemCard.SUPER_RARE_MIRAGE_FLOUR.item
                    11, 12 -> DemonItemCard.EXTREMELY_RARE_MIRAGE_FLOUR.item
                    else -> throw AssertionError()
                }
                ShapelessRecipeJsonBuilder
                    .create(fairyCard[1].item)
                    .input(DemonItemCard.XARPITE.item)
                    .input(mirageFlourItem)
                    .input(inputItem)
                    .criterion(inputItem)
                    .group(fairyCard[1].item)
                    .offerTo(it, "fairy/" concat fairyCard.motif)
            }
        }
    }
}
