package miragefairy2023.core.init.modules

import miragefairy2023.core.init.SlotContainer
import miragefairy2023.core.init.module
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.registerFuel
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.loot.v2.LootTableEvents
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.function.LootingEnchantLootFunction
import net.minecraft.loot.function.SetCountLootFunction
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.world.World


enum class DemonItemCard(
    val itemId: String,
    val enName: String,
    val jaName: String,
    val enPoem: String,
    val jaPoem: String,
) {
    XARPITE(
        "xarpite", "Xarpite", "紅天石",
        "Binds astral flux with magnetic force",
        "黒鉄の鎖は繋がれる。血腥い魂の檻へ。",
    ),
    MIRANAGITE(
        "miranagite", "Miranagite", "蒼天石",
        "Astral body crystallized by anti-entropy",
        "秩序の叛乱、天地創造の逆光。",
    ),
}

private val demonItems = SlotContainer<DemonItemCard, DemonItem>()
operator fun DemonItemCard.invoke() = demonItems[this]


val demonItemModule = module {

    // 全体
    DemonItemCard.values().forEach { card ->
        item(card.itemId, { DemonItem(FabricItemSettings().group(ItemGroup.MATERIALS)) }) {
            itemRegistration {
                demonItems[card] = item
            }

            englishTranslationGeneration { it.add(item, card.enName) }
            englishTranslationGeneration { it.add("${item.translationKey}.poem", card.enPoem) }
            japaneseTranslationGeneration { it.add(item, card.jaName) }
            japaneseTranslationGeneration { it.add("${item.translationKey}.poem", card.jaPoem) }

            itemModelGeneration { it.register(item, Models.GENERATED) }
        }
    }

    // 魔女→紅天石
    recipeRegistration {
        val lootTableId = EntityType.WITCH.lootTableId
        LootTableEvents.MODIFY.register { _, _, id, tableBuilder, source ->
            if (source.isBuiltin) {
                if (id == lootTableId) {
                    val itemEntry = ItemEntry.builder(DemonItemCard.XARPITE())
                    itemEntry.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(-1.0f, 1.0f), false))
                    itemEntry.apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0.0f, 1.0f)))
                    tableBuilder!!.pool(LootPool.builder().with(itemEntry))
                }
            }
        }
    }

    // 紅天石→松明
    recipeGeneration {
        ShapedRecipeJsonBuilder
            .create(Items.TORCH, 8)
            .input('A', DemonItemCard.XARPITE())
            .input('B', Items.STICK)
            .pattern("A")
            .pattern("B")
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .offerTo(it, Identifier.of(modId, "torch_from_xarpite"))
    }

    // 紅天石→燃料
    registerFuel({ DemonItemCard.XARPITE() }, 1600)

}


class DemonItem(settings: Settings) : Item(settings) {
    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)
        tooltip += text { translate("$translationKey.poem").gray }
    }
}
