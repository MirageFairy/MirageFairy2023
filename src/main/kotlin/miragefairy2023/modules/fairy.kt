package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.SlotContainer
import miragefairy2023.module
import miragefairy2023.util.aqua
import miragefairy2023.util.enJa
import miragefairy2023.util.enJaItem
import miragefairy2023.util.gold
import miragefairy2023.util.gray
import miragefairy2023.util.item
import miragefairy2023.util.red
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.server.RecipeProvider
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Optional
import java.util.UUID


enum class FairyCard(
    val motif: String,
    val rare: Int,
    val enName: String,
    val jaName: String,
    val skinColor: Int,
    val frontColor: Int,
    val backColor: Int,
    val hairColor: Int,
) {
    AIR("air", 0, "Airia", "空気精アイリャ", 0xFFBE80, 0xDEFFFF, 0xDEFFFF, 0xB0FFFF),
    DIRT("dirt", 1, "Dirtia", "土精ディルチャ", 0xB87440, 0xB9855C, 0x593D29, 0x914A18),
    SKELETON("skeleton", 2, "Skeletonia", "骸骨精スケレトーニャ", 0xCACACA, 0xCFCFCF, 0xCFCFCF, 0x494949),
    FOREST("forest", 3, "Forestia", "森精フォレスチャ", 0x80FF00, 0x7B9C62, 0x89591D, 0x2E6E14),
    IRON("iron", 4, "Ironia", "鉄精イローニャ", 0xA0A0A0, 0xD8D8D8, 0x727272, 0xD8AF93),
    PLAYER("player", 5, "Playeria", "人精プライェーリャ", 0xB58D63, 0x00AAAA, 0x322976, 0x4B3422),
    NIGHT("night", 6, "Nightia", "夜精ニグチャ", 0xFFE260, 0x2C2C2E, 0x0E0E10, 0x2D4272),
    WARDEN("warden", 7, "Wardenia", "監守者精ワルデーニャ", 0x0A3135, 0xCFCFA4, 0xA0AA7A, 0x2CD0CA),
    SUN("sun", 8, "Sunia", "太陽精スーニャ", 0xff2f00, 0xff972b, 0xff7500, 0xffe7b2),
    TIME("time", 9, "Timia", "時精ティーミャ", 0x89D585, 0xD5DEBC, 0xD8DEA7, 0x8DD586),
}

val FairyCard.identifier get() = Identifier(MirageFairy2023.modId, this.motif)

private val fairyItems = SlotContainer<FairyCard, Item>()
operator fun FairyCard.invoke() = fairyItems[this]


val fairyModule = module {

    // 妖精の共通アイテムモデル
    onGenerateItemModels {
        val layer0 = TextureKey.of("layer0")
        val layer1 = TextureKey.of("layer1")
        val layer2 = TextureKey.of("layer2")
        val layer3 = TextureKey.of("layer3")
        val layer4 = TextureKey.of("layer4")
        val model = Model(Optional.of(Identifier("minecraft", "item/generated")), Optional.empty(), layer0, layer1, layer2, layer3, layer4)
        model.upload(Identifier(modId, "item/fairy"), TextureMap().apply {
            put(layer0, Identifier(modId, "item/fairy_skin"))
            put(layer1, Identifier(modId, "item/fairy_back"))
            put(layer2, Identifier(modId, "item/fairy_front"))
            put(layer3, Identifier(modId, "item/fairy_hair"))
            put(layer4, Identifier(modId, "item/fairy_dress"))
        }, it.writer)
    }

    // 妖精登録
    FairyCard.values().forEach { fairyCard ->
        item("${fairyCard.motif}_fairy", { FairyItem(fairyCard, FabricItemSettings().group(commonItemGroup)) }) {
            onRegisterItems { fairyItems[fairyCard] = item }

            onGenerateItemModels { it.register(item, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
            onRegisterColorProvider { it ->
                it(item) { _, tintIndex ->
                    when (tintIndex) {
                        0 -> fairyCard.skinColor
                        1 -> fairyCard.backColor
                        2 -> fairyCard.frontColor
                        3 -> fairyCard.hairColor
                        4 -> 0xAA0000
                        else -> 0xFFFFFF
                    }
                }
            }

            enJaItem({ item }, fairyCard.enName, fairyCard.jaName)
        }
    }


    // 妖精ボーナス
    val fairyBonusUuid = UUID.fromString("378C9369-6CC3-4B45-AADD-5B221DF26ED0")
    ServerTickEvents.END_WORLD_TICK.register { world ->
        if ((world.time % (20L * 10L)).toInt() != 132) return@register // 10秒毎

        world.players.forEach { player ->

            // 有効な妖精のリスト
            val triples = (player.inventory.offHand + player.inventory.main.slice(9 * 3 until 9 * 4))
                .mapNotNull { itemStack ->
                    itemStack!!
                    val item = itemStack.item
                    if (item !is FairyProviderItem) return@mapNotNull null
                    val fairy = item.getFairy()
                    Triple(itemStack, fairy, fairy.getIdentifier())
                }
                .distinctBy {
                    it.second.getIdentifier()
                }


            val entityAttributeInstance = player.attributes.getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED) ?: return@forEach

            // 古い効果を削除
            entityAttributeInstance.removeModifier(fairyBonusUuid)

            // 新しい効果を付与
            val speedBonus = triples.mapNotNull { it.second.getSpeedBonus(player) }.sum()
            if (speedBonus != 0.0) {
                val entityAttributeModifier = EntityAttributeModifier(fairyBonusUuid, "effect.$modId.fairy_bonus", speedBonus, EntityAttributeModifier.Operation.MULTIPLY_BASE)
                entityAttributeInstance.addTemporaryModifier(entityAttributeModifier)
            }

        }

    }
    enJa("effect.$modId.fairy_bonus", "Fairy Bonus", "妖精ボーナス")

    // パッシブスキル翻訳
    enJa(FairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY, "Use passive skill in 3rd row of inventory", "インベントリの3行目でパッシブスキルを発動")
    enJa(FairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY, "Same fairies exist", "妖精が重複しています")
    enJa(FairyItem.UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY, "Passive skill is unavailable", "パッシブスキル利用不可")
    enJa(FairyItem.AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY, "Passive skill is active", "パッシブスキル発動中")
    enJa(FairyItem.OVERWORLD_CONDITION_KEY, "Overworld", "地上世界")
    enJa(FairyItem.IN_AIR_CONDITION_KEY, "In the Air", "空気中")
    enJa(FairyItem.MOVEMENT_SPEED_EFFECT_KEY, "Movement Speed", "移動速度")
    enJa(FairyItem.RARE_KEY, "Rare", "レア度")

    // 紅天石＋土→土精
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(FairyCard.DIRT())
            .input(DemonItemCard.XARPITE())
            .input(Items.DIRT)
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .criterion("has_dirt", RecipeProvider.conditionsFromItem(Items.DIRT))
            .offerTo(it, Identifier.of(modId, "fairy/dirt"))
    }

    // 紅天石＋鉄インゴット→鉄精
    onGenerateRecipes {
        ShapelessRecipeJsonBuilder
            .create(FairyCard.IRON())
            .input(DemonItemCard.XARPITE())
            .input(Items.IRON_INGOT)
            .criterion("has_xarpite", RecipeProvider.conditionsFromItem(DemonItemCard.XARPITE()))
            .criterion("has_iron_ingot", RecipeProvider.conditionsFromItem(Items.IRON_INGOT))
            .offerTo(it, Identifier.of(modId, "fairy/iron"))
    }

}


class PassiveFairy(val player: PlayerEntity, val index: Int, val itemStack: ItemStack, val fairy: Fairy, val fairyIdentifier: Identifier, val isDuplicated: Boolean)

private fun PlayerEntity.getPassiveFairies(): List<PassiveFairy> {
    val itemStacks = this.inventory.offHand + this.inventory.main.slice(9 * 3 until 9 * 4)
    val result = mutableListOf<PassiveFairy>()
    val collectedFairyIdentifiers = mutableSetOf<Identifier>()
    itemStacks.forEachIndexed { index, itemStack ->
        itemStack!!
        val item = itemStack.item
        if (item !is FairyProviderItem) return@forEachIndexed
        val fairy = item.getFairy()
        val fairyIdentifier = fairy.getIdentifier()
        val isDuplicated = fairyIdentifier in collectedFairyIdentifiers
        collectedFairyIdentifiers += fairyIdentifier
        result += PassiveFairy(this, index, itemStack, fairy, fairyIdentifier, isDuplicated)
    }
    return result.toList()
}


interface Fairy {
    fun getIdentifier(): Identifier
    fun getSpeedBonus(player: ServerPlayerEntity): Double?
}

interface FairyProviderItem {
    fun getFairy(): Fairy
}


class FairyItem(val fairyCard: FairyCard, settings: Settings) : Item(settings), FairyProviderItem {
    companion object {
        val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.disabled"
        val DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.duplicated"
        val UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.unavailable"
        val AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.available"
        val OVERWORLD_CONDITION_KEY = "item.${MirageFairy2023.modId}.passive_skill.condition.overworld"
        val IN_AIR_CONDITION_KEY = "item.${MirageFairy2023.modId}.passive_skill.condition.in_air"
        val MOVEMENT_SPEED_EFFECT_KEY = "item.${MirageFairy2023.modId}.passive_skill.effect.movement_speed"
        val RARE_KEY = "item.${MirageFairy2023.modId}.rare"
    }

    private fun isOverworld(player: PlayerEntity) = player.world.dimension.natural

    private fun isInAir(player: PlayerEntity): Boolean {
        val blockState = player.world.getBlockState(BlockPos(player.eyePos))
        return !blockState.isOpaque && blockState.fluidState.isEmpty
    }

    override fun getFairy() = object : Fairy {
        override fun getIdentifier() = fairyCard.identifier
        override fun getSpeedBonus(player: ServerPlayerEntity) = if (isOverworld(player) && isInAir(player)) 0.05 else null
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        tooltip += text { (translate(RARE_KEY) + ": ${fairyCard.rare}"()).aqua }

        val player = MirageFairy2023.proxy?.getClientPlayer() ?: return

        val passiveFairies = player.getPassiveFairies().find { it.itemStack === stack }

        val isEnabled = passiveFairies != null
        val isDuplicated = passiveFairies != null && passiveFairies.isDuplicated
        val isOverworld = isOverworld(player)
        val isInAir = isInAir(player)
        val isAvailable = isEnabled && !isDuplicated && isOverworld && isInAir
        tooltip += text {
            when {
                isAvailable -> translate(AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY).gold
                isEnabled && !isDuplicated -> translate(UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY).red
                isEnabled -> translate(DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY).red
                else -> translate(DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY).gray
            }
        }

        tooltip += text {
            val text = translate(MOVEMENT_SPEED_EFFECT_KEY) + " "() + (0.05 * 100 formatAs "%+.0f%%")() + " ["() + when {
                isOverworld -> translate(OVERWORLD_CONDITION_KEY)
                else -> translate(OVERWORLD_CONDITION_KEY).red
            } + ","() + when {
                isInAir -> translate(IN_AIR_CONDITION_KEY)
                else -> translate(IN_AIR_CONDITION_KEY).red
            } + "]"()
            if (isAvailable) text.gold else text.gray
        }

    }
}