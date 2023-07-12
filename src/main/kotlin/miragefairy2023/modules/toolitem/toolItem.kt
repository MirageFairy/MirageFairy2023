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
import miragefairy2023.modules.passiveskill.always
import miragefairy2023.modules.passiveskill.mana
import miragefairy2023.modules.passiveskill.passiveSkills
import miragefairy2023.modules.registerPoemList
import miragefairy2023.util.Translation
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.group
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
import net.minecraft.util.registry.Registry

class ToolItemCard<T : Item>(
    val path: String,
    val enName: String,
    val jaName: String,
    val poemList: List<Poem>,
    val initializer: ToolMaterialCardInitializer<T>,
) {
    init {
        values += this
    }

    val identifier = Identifier(MirageFairy2023.modId, path)
    val item = initializer.createItem()

    companion object {
        val values = mutableListOf<ToolItemCard<*>>()

        val DREAM_CATCHER = ToolItemCard(
            "dream_catcher", "Dream Catcher", "ドリームキャッチャー",
            listOf(
                Poem("Tool to capture the free astral vortices", "未知なる記憶が、ほらそこに。"),
                Description("description1", "Show fairy dreams when in inventory", "インベントリ内に所持時、妖精の夢を表示"),
                Description("description2", "Acquire the fairy dream when used", "使用時、妖精の夢を獲得"),
            ),
            DreamCatcherInitializer(ToolMaterialCard.MIRAGE, 20),
        )
        val BLUE_DREAM_CATCHER = ToolItemCard(
            "blue_dream_catcher", "Blue Dream Catcher", "蒼天のドリームキャッチャー",
            listOf(
                Poem("What are good memories for you?", "信愛、悲哀、混沌の果て。"),
                Description("description1", "Show fairy dreams when in inventory", "インベントリ内に所持時、妖精の夢を表示"),
                Description("description2", "Acquire the fairy dream when used", "使用時、妖精の夢を獲得"),
            ),
            DreamCatcherInitializer(ToolMaterialCard.CHAOS_STONE, 400),
        )
        val ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE = ToolItemCard(
            "artificial_fairy_crystal_pickaxe", "Crystal Pickaxe", "クリスタルのつるはし",
            listOf(Poem("Amorphous mental body of fairies", "妖精さえ怖れる、技術の結晶。")),
            PickaxeInitializer(ToolMaterialCard.ARTIFICIAL_FAIRY_CRYSTAL, BlockTags.PICKAXE_MINEABLE),
        )
        val ARTIFICIAL_FAIRY_CRYSTAL_PENDANT = ToolItemCard(
            "artificial_fairy_crystal_pendant", "Crystal Pendant", "クリスタルのペンダント",
            listOf(Poem("Object that makes Mirage fairies fairies", "『妖精』だったあのころ――")),
            PassiveSkillAccessoryInitializer(listOf(TrinketsSlotCard.CHEST_NECKLACE), 5.0, passiveSkills {
                mana(0.4) on always()
            }),
        )
        val MIRANAGITE_KNIFE = ToolItemCard(
            "miranagite_knife", "Miranagi Knife", "蒼天のナイフ",
            listOf(
                Poem("Gardener's tool invented by Miranagi", "大自然を駆ける探究者のナイフ。"),
                Description("Enchant silk touch when using raw item", "生のアイテム使用時、シルクタッチ付与"),
            ),
            KnifeInitializer(ToolMaterialCard.MIRANAGITE, silkTouch = true),
        )
        val MIRANAGITE_PICKAXE = ToolItemCard(
            "miranagite_pickaxe", "Miranagi Pickaxe", "蒼天のつるはし",
            listOf(
                Poem("Promotes ore recrystallization", "凝集する秩序、蒼穹彩煌が如く。"),
                Description("Enchant silk touch when using raw item", "生のアイテム使用時、シルクタッチ付与"),
            ),
            PickaxeInitializer(ToolMaterialCard.MIRANAGITE, BlockTags.PICKAXE_MINEABLE, silkTouch = true),
        )
        val MIRANAGITE_STAFF = ToolItemCard(
            "miranagite_staff", "Miranagi Staff", "みらなぎの杖",
            listOf(Poem("Risk of vacuum decay due to anti-entropy", "創世の神光は混沌をも翻す。")),
            StaffInitializer(ToolMaterialCard.MIRANAGITE),
        )
        val CHAOS_STONE_PICKAXE = ToolItemCard(
            "chaos_stone_pickaxe", "Chaos Pickaxe", "混沌のつるはし",
            listOf(
                Poem("Is this made of metal? Or clay?", "時空結晶の交点に、古代の産業が芽吹く。"),
                Description("Can dig like a shovel", "シャベルのように掘れる"),
            ),
            PickaxeInitializer(ToolMaterialCard.CHAOS_STONE, BlockTags.PICKAXE_MINEABLE, BlockTags.SHOVEL_MINEABLE),
        )
        val CHAOS_FISHING_GROVE = ToolItemCard(
            "chaos_fishing_grove", "Chaos Fishing Grove", "混沌のフィッシンググローブ",
            listOf(
                Poem("The impurity named automation", "古代、混沌は余裕と幸福をもたらした。"),
                Description("Automatic fishing", "釣りを自動進行"),
                Penalty("penalty1", "Consume more fishing rod durability", "釣り竿の耐久値消費増加"),
                Penalty("penalty2", "Halves fishing enchantments", "釣りのエンチャント効果半減"),
            ),
            TrinketAccessoryInitializer(listOf(TrinketsSlotCard.HAND_GLOVE, TrinketsSlotCard.OFFHAND_GLOVE)) { FishingGroveItem(it) },
        )
    }
}

val toolItemModule = module {

    // 全体
    fun <T : Item> init(card: ToolItemCard<T>) {
        card.initializer.run { this@module.init(card) }
    }
    ToolItemCard.values.forEach { card ->
        Registry.register(Registry.ITEM, card.identifier, card.item)
        init(card)
    }

    // ドリームキャッチャー
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.DREAM_CATCHER.item)
            .pattern("FSS")
            .pattern("FSS")
            .pattern("RFF")
            .input('F', Items.FEATHER)
            .input('S', Items.STRING)
            .input('R', DemonItemCard.MIRAGE_STEM.item)
            .criterion(DemonItemCard.MIRAGE_STEM.item)
            .group(ToolItemCard.DREAM_CATCHER.item)
            .offerTo(it, ToolItemCard.DREAM_CATCHER.item.identifier)
    }

    // 蒼天のドリームキャッチャー
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.BLUE_DREAM_CATCHER.item)
            .pattern("GII")
            .pattern("G#I")
            .pattern("IGG")
            .input('#', ToolItemCard.DREAM_CATCHER.item)
            .input('G', DemonItemCard.MIRANAGITE.item)
            .input('I', DemonItemCard.CHAOS_STONE.item)
            .criterion(ToolItemCard.DREAM_CATCHER.item)
            .group(ToolItemCard.BLUE_DREAM_CATCHER.item)
            .offerTo(it, ToolItemCard.BLUE_DREAM_CATCHER.item.identifier)
    }

    // クリスタルのつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .input('S', Items.STICK)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PICKAXE.item.identifier)
    }

    // クリスタルのペンダント
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item)
            .pattern(" s ")
            .pattern("s s")
            .pattern(" G ")
            .input('G', DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .input('s', Items.STRING)
            .criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            .group(ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item)
            .offerTo(it, ToolItemCard.ARTIFICIAL_FAIRY_CRYSTAL_PENDANT.item.identifier)
    }

    // 蒼天のナイフ
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.MIRANAGITE_KNIFE.item)
            .pattern("G")
            .pattern("S")
            .input('G', DemonItemCard.MIRANAGITE.item)
            .input('S', Items.STICK)
            .criterion(DemonItemCard.MIRANAGITE.item)
            .group(ToolItemCard.MIRANAGITE_KNIFE.item)
            .offerTo(it, ToolItemCard.MIRANAGITE_KNIFE.item.identifier)
    }

    // 蒼天のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.MIRANAGITE_PICKAXE.item)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.MIRANAGITE.item)
            .input('S', Items.STICK)
            .criterion(DemonItemCard.MIRANAGITE.item)
            .group(ToolItemCard.MIRANAGITE_PICKAXE.item)
            .offerTo(it, ToolItemCard.MIRANAGITE_PICKAXE.item.identifier)
    }

    // 蒼天のスタッフ
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.MIRANAGITE_STAFF.item)
            .pattern(" ID")
            .pattern(" RI")
            .pattern("N  ")
            .input('R', DemonItemCard.MIRANAGITE_ROD.item)
            .input('D', Items.DIAMOND)
            .input('I', Items.IRON_INGOT)
            .input('N', Items.IRON_NUGGET)
            .criterion(DemonItemCard.MIRANAGITE.item)
            .group(ToolItemCard.MIRANAGITE_STAFF.item)
            .offerTo(it, ToolItemCard.MIRANAGITE_STAFF.item.identifier)
    }

    // 混沌のつるはし
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_STONE_PICKAXE.item)
            .pattern("GGG")
            .pattern(" S ")
            .pattern(" S ")
            .input('G', DemonItemCard.CHAOS_STONE.item)
            .input('S', Items.STICK)
            .criterion(DemonItemCard.CHAOS_STONE.item)
            .group(ToolItemCard.CHAOS_STONE_PICKAXE.item)
            .offerTo(it, ToolItemCard.CHAOS_STONE_PICKAXE.item.identifier)
    }

    // 混沌のフィッシンググローブ
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(ToolItemCard.CHAOS_FISHING_GROVE.item)
            .pattern(" GG")
            .pattern("GGG")
            .pattern(" GG")
            .input('G', DemonItemCard.CHAOS_STONE.item)
            .criterion(DemonItemCard.CHAOS_STONE.item)
            .group(ToolItemCard.CHAOS_FISHING_GROVE.item)
            .offerTo(it, ToolItemCard.CHAOS_FISHING_GROVE.item.identifier)
    }

    enJa(DreamCatcherItem.knownKey)
    enJa(DreamCatcherItem.successKey)
    enJa(NOT_ENOUGH_EXPERIENCE_KEY)

}


val NOT_ENOUGH_EXPERIENCE_KEY = Translation("item.miragefairy2023.magic.not_enough_experience", "Not enough experience", "経験値が足りません")
val DREAM_CATCHERS: TagKey<Item> = TagKey.of(Registry.ITEM_KEY, Identifier(MirageFairy2023.modId, "dream_catchers"))


interface ToolMaterialCardInitializer<T : Item> {
    fun createItem(): T
    fun InitializationScope.init(card: ToolItemCard<T>)
}

class DreamCatcherInitializer(private val toolMaterialCard: ToolMaterialCard, private val maxDamage: Int) : ToolMaterialCardInitializer<DreamCatcherItem> {
    override fun createItem() = DreamCatcherItem(toolMaterialCard.toolMaterial, maxDamage, FabricItemSettings().group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<DreamCatcherItem>) {
        onGenerateItemModels { it.register(card.item, Models.HANDHELD) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(card.item) }
        onGenerateItemTags { it(DREAM_CATCHERS).add(card.item) }
    }
}

class KnifeInitializer(private val toolMaterialCard: ToolMaterialCard, private val silkTouch: Boolean = false) : ToolMaterialCardInitializer<DemonKnifeItem> {
    override fun createItem() = DemonKnifeItem(toolMaterialCard.toolMaterial, silkTouch, FabricItemSettings().group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<DemonKnifeItem>) {
        onGenerateItemModels { it.register(card.item, Models.HANDHELD) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(card.item) }
    }
}

class PickaxeInitializer(private val toolMaterialCard: ToolMaterialCard, private vararg val effectiveBlockTags: TagKey<Block>, private val silkTouch: Boolean = false) : ToolMaterialCardInitializer<DemonPickaxeItem> {
    override fun createItem() = DemonPickaxeItem(toolMaterialCard.toolMaterial, 1, -2.8F, effectiveBlockTags.toList(), silkTouch, FabricItemSettings().group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<DemonPickaxeItem>) {
        onGenerateItemModels { it.register(card.item, Models.HANDHELD) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(card.item) }
        onGenerateItemTags { it(ItemTags.CLUSTER_MAX_HARVESTABLES).add(card.item) }
        onGenerateItemTags { it(ConventionalItemTags.PICKAXES).add(card.item) }
    }
}

class StaffInitializer(private val toolMaterialCard: ToolMaterialCard) : ToolMaterialCardInitializer<StaffItem> {
    override fun createItem() = StaffItem(toolMaterialCard.toolMaterial, FabricItemSettings().group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<StaffItem>) {
        onGenerateItemModels { it.register(card.item, Models.HANDHELD) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(card.item) }
    }
}

class PassiveSkillAccessoryInitializer(private val trinketsSlotCards: List<TrinketsSlotCard>, private val mana: Double, private val passiveSkills: List<PassiveSkill>) : ToolMaterialCardInitializer<PassiveSkillAccessoryItem> {
    override fun createItem() = PassiveSkillAccessoryItem(mana, passiveSkills, FabricItemSettings().maxCount(1).group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<PassiveSkillAccessoryItem>) {
        onGenerateItemModels { it.register(card.item, Models.GENERATED) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        trinketsSlotCards.forEach { trinketsSlotCard ->
            onGenerateItemTags { it(trinketsSlotCard.tag).add(card.item) }
        }
    }
}

class TrinketAccessoryInitializer<I>(private val trinketsSlotCards: List<TrinketsSlotCard>, private val itemCreator: (Item.Settings) -> I) : ToolMaterialCardInitializer<I> where I : Item, I : Trinket {
    override fun createItem() = itemCreator(FabricItemSettings().maxCount(1).group(commonItemGroup))
    override fun InitializationScope.init(card: ToolItemCard<I>) {
        onGenerateItemModels { it.register(card.item, Models.GENERATED) }
        enJa(card.item, card.enName, card.jaName)
        generatePoemList(card.item, card.poemList)
        onRegisterItems { registerPoemList(card.item, card.poemList) }
        trinketsSlotCards.forEach { trinketsSlotCard ->
            onGenerateItemTags { it(trinketsSlotCard.tag).add(card.item) }
        }
        onRegisterItems { TrinketsApi.registerTrinket(card.item, card.item) }
    }
}
