package miragefairy2023.modules.toolitem

import dev.emi.trinkets.api.Trinket
import dev.emi.trinkets.api.TrinketsApi
import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.module
import miragefairy2023.modules.DemonItemCard
import miragefairy2023.modules.Description
import miragefairy2023.modules.Penalty
import miragefairy2023.modules.Poem
import miragefairy2023.modules.ToolMaterialCard
import miragefairy2023.modules.TrinketsSlotCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.modules.generatePoemList
import miragefairy2023.modules.invoke
import miragefairy2023.modules.passiveskill.ManaPassiveSkillEffect
import miragefairy2023.modules.registerPoemList
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.minecraft.block.Block
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier

enum class ToolItemCard(
    val path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
    val initializer: InitializationScope.(ToolItemCard) -> Unit,
) {
    DREAM_CATCHER(
        "dream_catcher", "Dream Catcher", "ドリームキャッチャー",
        listOf(
            Poem("Tool to capture the free astral vortices", "未知なる記憶が、ほらそこに。"),
            Description("description1", "Show fairy dreams when in inventory", "インベントリ内に所持時、妖精の夢を表示"),
            Description("description2", "Acquire the fairy dream when used", "使用時、妖精の夢を獲得"),
        ),
        dreamCatcher(ToolMaterialCard.MIRAGE, 20),
    ),
    BLUE_DREAM_CATCHER(
        "blue_dream_catcher", "Blue Dream Catcher", "蒼天のドリームキャッチャー",
        listOf(
            Poem("What are good memories for you?", "信愛、悲哀、混沌の果て。"),
            Description("description1", "Show fairy dreams when in inventory", "インベントリ内に所持時、妖精の夢を表示"),
            Description("description2", "Acquire the fairy dream when used", "使用時、妖精の夢を獲得"),
        ),
        dreamCatcher(ToolMaterialCard.CHAOS_STONE, 400),
    ),
    ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE(
        "artificial_fairy_crystal_pickaxe", "Crystal Pickaxe", "クリスタルのつるはし",
        listOf(Poem("Amorphous mental body of fairies", "妖精さえ怖れる、技術の結晶。")),
        pickaxe(ToolMaterialCard.ARTIFICIAL_FAIRY_CRYSTAL, 1, -2.8F, BlockTags.PICKAXE_MINEABLE),
    ),
    ARTIFICIAL_FAIRY_CRYSTAL_PENDANT(
        "artificial_fairy_crystal_pendant", "Crystal Pendant", "クリスタルのペンダント",
        listOf(Poem("Object that makes Mirage fairies fairies", "『妖精』だったあのころ――")),
        passiveSkillAccessory(listOf(TrinketsSlotCard.CHEST_NECKLACE), 5.0, buildList {
            this += PassiveSkill(listOf(), ManaPassiveSkillEffect(0.4))
        }),
    ),
    MIRANAGITE_PICKAXE(
        "miranagite_pickaxe", "Miranagi Pickaxe", "蒼天のつるはし",
        listOf(
            Poem("Promotes ore recrystallization", "凝集する秩序、蒼穹彩煌が如く。"),
            Description("Enchant silk touch when using raw item", "生のアイテム使用時、シルクタッチ付与"),
        ),
        pickaxe(ToolMaterialCard.MIRANAGITE, 1, -2.8F, BlockTags.PICKAXE_MINEABLE, silkTouch = true),
    ),
    CHAOS_STONE_PICKAXE(
        "chaos_stone_pickaxe", "Chaos Pickaxe", "混沌のつるはし",
        listOf(
            Poem("Is this made of metal? Or clay?", "時空結晶の交点に、古代の産業が芽吹く。"),
            Description("Can dig like a shovel", "シャベルのように掘れる"),
        ),
        pickaxe(ToolMaterialCard.CHAOS_STONE, 1, -2.8F, BlockTags.PICKAXE_MINEABLE, BlockTags.SHOVEL_MINEABLE),
    ),
    CHAOS_FISHING_GROVE(
        "chaos_fishing_grove", "Chaos Fishing Grove", "混沌のフィッシンググローブ",
        listOf(
            Poem("The impurity named automation", "古代、混沌は余裕と幸福をもたらした。"),
            Description("Automatic fishing", "釣りを自動進行"),
            Penalty("penalty1", "Consume more fishing rod durability", "釣り竿の耐久値消費増加"),
            Penalty("penalty2", "Halves fishing enchantments", "釣りのエンチャント効果半減"),
        ),
        trinketAccessory(listOf(TrinketsSlotCard.HAND_GLOVE, TrinketsSlotCard.OFFHAND_GLOVE)) { FishingGroveItem(it) },
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    lateinit var item: FeatureSlot<Item>
}

val toolItemModule = module {

    // 全体
    ToolItemCard.values().forEach { card ->
        card.initializer(this, card)
    }

    // ドリームキャッチャー
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.DREAM_CATCHER.item.feature)
            .pattern("FSS")
            .pattern("FSS")
            .pattern("RFF")
            .input('F', Items.FEATHER)
            .input('S', Items.STRING)
            .input('R', DemonItemCard.MIRAGE_STEM())
            .criterion(DemonItemCard.MIRAGE_STEM())
            .group(ToolItemCard.DREAM_CATCHER.item.feature)
            .offerTo(it, ToolItemCard.DREAM_CATCHER.item.feature.identifier)
    }

    // 蒼天のドリームキャッチャー
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.BLUE_DREAM_CATCHER.item.feature)
            .pattern("GII")
            .pattern("G#I")
            .pattern("IGG")
            .input('#', ToolItemCard.DREAM_CATCHER.item.feature)
            .input('G', DemonItemCard.MIRANAGITE())
            .input('I', DemonItemCard.CHAOS_STONE())
            .criterion(ToolItemCard.DREAM_CATCHER.item.feature)
            .group(ToolItemCard.BLUE_DREAM_CATCHER.item.feature)
            .offerTo(it, ToolItemCard.BLUE_DREAM_CATCHER.item.feature.identifier)
    }

    // クリスタルのつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.feature.identifier)
    }

    // クリスタルのペンダント
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature)
            .pattern(" s ")
            .pattern("s s")
            .pattern(" G ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .input('s', Items.STRING)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL())
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.feature.identifier)
    }

    // 蒼天のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.MIRANAGITE_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.MIRANAGITE())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.MIRANAGITE())
            .group(ToolItemCard.MIRANAGITE_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.MIRANAGITE_PICKAXE.item.feature.identifier)
    }

    // 混沌のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.CHAOS_STONE())
            .input('S', Items.STICK)
            .criterion(DemonItemCard.CHAOS_STONE())
            .group(ToolItemCard.CHAOS_STONE_PICKAXE.item.feature)
            .offerTo(it, ToolItemCard.CHAOS_STONE_PICKAXE.item.feature.identifier)
    }

    // 混沌のフィッシンググローブ
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_FISHING_GROVE.item.feature)
            .pattern(" GG")
            .pattern("GGG")
            .pattern(" GG")
            .input('G', DemonItemCard.CHAOS_STONE())
            .criterion(DemonItemCard.CHAOS_STONE())
            .group(ToolItemCard.CHAOS_FISHING_GROVE.item.feature)
            .offerTo(it, ToolItemCard.CHAOS_FISHING_GROVE.item.feature.identifier)
    }

    translation(DreamCatcherItem.knownKey)
    translation(DreamCatcherItem.successKey)

}


private fun dreamCatcher(toolMaterialCard: ToolMaterialCard, maxDamage: Int): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, { DreamCatcherItem(toolMaterialCard.toolMaterial, maxDamage, FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(feature) }
    }
}

private fun pickaxe(toolMaterialCard: ToolMaterialCard, attackDamage: Int, attackSpeed: Float, vararg effectiveBlockTags: TagKey<Block>, silkTouch: Boolean = false): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, { DemonPickaxeItem(toolMaterialCard.toolMaterial, attackDamage, attackSpeed, effectiveBlockTags.toList(), silkTouch, FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(feature) }
        onGenerateItemTags { it(ItemTags.CLUSTER_MAX_HARVESTABLES).add(feature) }
        onGenerateItemTags { it(ConventionalItemTags.PICKAXES).add(feature) }
    }
}

private fun passiveSkillAccessory(trinketsSlotCards: List<TrinketsSlotCard>, mana: Double, passiveSkills: List<PassiveSkill>): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, { PassiveSkillAccessoryItem(mana, passiveSkills, FabricItemSettings().maxCount(1).group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.GENERATED) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        trinketsSlotCards.forEach { trinketsSlotCard ->
            onGenerateItemTags { it(trinketsSlotCard.tag).add(feature) }
        }
    }
}

private fun <I> trinketAccessory(trinketsSlotCards: List<TrinketsSlotCard>, itemCreator: (Item.Settings) -> I): InitializationScope.(ToolItemCard) -> Unit where I : Item, I : Trinket = { card ->
    card.item = item(card.path, { itemCreator(FabricItemSettings().maxCount(1).group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Models.GENERATED) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        trinketsSlotCards.forEach { trinketsSlotCard ->
            onGenerateItemTags { it(trinketsSlotCard.tag).add(feature) }
        }
        onRegisterItems { TrinketsApi.registerTrinket(feature, feature) }
    }
}