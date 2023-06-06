package miragefairy2023.modules

import dev.emi.trinkets.api.TrinketItem
import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.api.PassiveSkill
import miragefairy2023.api.PassiveSkillItem
import miragefairy2023.api.PassiveSkillProvider
import miragefairy2023.module
import miragefairy2023.modules.passiveskill.ManaPassiveSkillEffect
import miragefairy2023.modules.passiveskill.getPassiveSkillTooltip
import miragefairy2023.util.identifier
import miragefairy2023.util.init.FeatureSlot
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJaItem
import miragefairy2023.util.init.group
import miragefairy2023.util.init.item
import miragefairy2023.util.init.translation
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.fabricmc.yarn.constants.MiningLevels
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Models
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PickaxeItem
import net.minecraft.item.Vanishable
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import net.minecraft.tag.TagKey
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

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
        accessory(TrinketsSlotCard.CHEST_NECKLACE, 5.0, buildList {
            this += PassiveSkill(listOf(), ManaPassiveSkillEffect(0.4))
        }),
    ),
    MIRANAGITE_PICKAXE(
        "miranagite_pickaxe", "Miranagi Pickaxe", "蒼天のつるはし",
        listOf(
            Poem("Promotes ore recrystallization", "凝集する秩序、蒼穹彩煌が如く。"),
            Description("Enchant silk touch when using raw item", "生のアイテム使用時、シルクタッチ付与")
        ),
        pickaxe(ToolMaterialCard.MIRANAGITE, 1, -2.8F, BlockTags.PICKAXE_MINEABLE, silkTouch = true),
    ),
    CHAOS_STONE_PICKAXE(
        "chaos_stone_pickaxe", "Chaos Pickaxe", "混沌のつるはし",
        listOf(
            Poem("Is this made of metal? Or clay?", "時空結晶の交点に、古代の産業が芽吹く。"),
            Description("Can dig like a shovel", "シャベルのように掘れる")
        ),
        pickaxe(ToolMaterialCard.CHAOS_STONE, 1, -2.8F, BlockTags.PICKAXE_MINEABLE, BlockTags.SHOVEL_MINEABLE),
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
    card.item = item(card.path, {
        object : PickaxeItem(toolMaterialCard.toolMaterial, attackDamage, attackSpeed, FabricItemSettings().group(commonItemGroup)) {
            override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState) = if (effectiveBlockTags.any { state.isIn(it) }) miningSpeed else 1.0F

            override fun isSuitableFor(state: BlockState): Boolean {
                val itemMiningLevel = material.miningLevel
                return when {
                    itemMiningLevel < MiningLevels.DIAMOND && state.isIn(BlockTags.NEEDS_DIAMOND_TOOL) -> false
                    itemMiningLevel < MiningLevels.IRON && state.isIn(BlockTags.NEEDS_IRON_TOOL) -> false
                    itemMiningLevel < MiningLevels.STONE && state.isIn(BlockTags.NEEDS_STONE_TOOL) -> false
                    else -> effectiveBlockTags.any { state.isIn(it) }
                }
            }

            override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
                if (world.isClient) return super.use(world, user, hand)
                if (silkTouch) {
                    val itemStack = user.getStackInHand(hand)
                    if (EnchantmentHelper.get(itemStack).isEmpty()) {
                        if (user.isCreative || user.experienceLevel >= 5) {
                            if (!user.isCreative) user.addExperienceLevels(-5)
                            EnchantmentHelper.set(mapOf(Enchantments.SILK_TOUCH to 1), itemStack)
                            world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F)
                        }
                    }
                    return TypedActionResult.consume(itemStack)
                }
                return super.use(world, user, hand)
            }
        }
    }) {
        onGenerateItemModels { it.register(feature, Models.HANDHELD) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        onGenerateItemTags { it(toolMaterialCard.tag).add(feature) }
        onGenerateItemTags { it(ItemTags.CLUSTER_MAX_HARVESTABLES).add(feature) }
        onGenerateItemTags { it(ConventionalItemTags.PICKAXES).add(feature) }
    }
}

private fun accessory(trinketsSlotCard: TrinketsSlotCard, mana: Double, passiveSkills: List<PassiveSkill>): InitializationScope.(ToolItemCard) -> Unit = { card ->
    card.item = item(card.path, {
        class PassiveSkillAccessoryItem : Item(FabricItemSettings().group(commonItemGroup).maxCount(1)), PassiveSkillItem, Vanishable {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += getPassiveSkillTooltip(stack, mana, mana, passiveSkills)
            }

            override val passiveSkillProvider: PassiveSkillProvider
                get() = object : PassiveSkillProvider {
                    override val identifier get() = card.identifier
                    override val mana get() = mana
                    override fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack) = passiveSkills
                }

            override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
                val itemStack = user.getStackInHand(hand)
                if (TrinketItem.equipItem(user, itemStack)) {
                    return TypedActionResult.success(itemStack, world.isClient)
                }
                return super.use(world, user, hand)
            }

            override fun getEquipSound(): SoundEvent = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND
        }
        PassiveSkillAccessoryItem()
    }) {
        onGenerateItemModels { it.register(feature, Models.GENERATED) }
        enJaItem({ feature }, card.enName, card.jaName)
        generatePoemList(card.poemList)
        onRegisterItems { registerPoemList(feature, card.poemList) }
        onGenerateItemTags { it(trinketsSlotCard.tag).add(feature) }
    }
}
