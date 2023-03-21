package miragefairy2023

import miragefairy2023.core.init.InitializationScope
import miragefairy2023.core.init.Slot
import miragefairy2023.util.gray
import miragefairy2023.util.registerFuel
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.unit
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
import net.minecraft.util.registry.Registry
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

private val demonItems = DemonItemCard.values().associateWith { Slot<Item>() }
operator fun DemonItemCard.invoke() = demonItems[this]!!.item
operator fun DemonItemCard.invoke(item: Item) = unit { demonItems[this]!!.item = item }


fun InitializationScope.initDemonItem() {

    // 全体
    DemonItemCard.values().forEach { card ->

        itemRegistration += {
            val item = object : Item(FabricItemSettings().group(ItemGroup.MATERIALS)) {
                override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                    super.appendTooltip(stack, world, tooltip, context)
                    tooltip += text { translate("item.$modId.${card.itemId}.poem").gray }
                }
            }
            card(item)
            Registry.register(Registry.ITEM, Identifier(modId, card.itemId), item)
        }

        englishTranslationGeneration += { it.add(card(), card.enName) }
        englishTranslationGeneration += { it.add(card().translationKey + ".poem", card.enPoem) }
        japaneseTranslationGeneration += { it.add(card(), card.jaName) }
        japaneseTranslationGeneration += { it.add(card().translationKey + ".poem", card.jaPoem) }

        itemModelGeneration += { it.register(card(), Models.GENERATED) }

    }

    // 魔女→紅天石
    recipeRegistration += {
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
    recipeGeneration += {
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
