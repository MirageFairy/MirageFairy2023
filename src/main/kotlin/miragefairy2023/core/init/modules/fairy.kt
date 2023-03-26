package miragefairy2023.core.init.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.core.init.module
import miragefairy2023.util.aqua
import miragefairy2023.util.enJa
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
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Optional
import java.util.UUID

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
    item("air_fairy", { FairyItem(FabricItemSettings().group(ItemGroup.MATERIALS)) }) {
        onGenerateItemModels { it.register(item, Model(Optional.of(Identifier(modId, "item/fairy")), Optional.empty())) }
        onRegisterColorProvider { it ->
            it(item) { _, tintIndex ->
                when (tintIndex) {
                    0 -> 0xFFCCCC
                    1 -> 0xFF6666
                    2 -> 0xFF6666
                    3 -> 0xFF4444
                    4 -> 0xAA0000
                    else -> 0xFFFFFF
                }
            }
        }
        enJa("Airia", "空気精アイリャ")
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
    enJa(FairyItem.DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY, "Put on 3rd row of inventory to activate passive skill", "インベントリの3行目でパッシブスキルを発動")
    enJa(FairyItem.DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY, "Same fairies exist", "妖精が重複しています")
    enJa(FairyItem.UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY, "Passive skill is unavailable", "パッシブスキル利用不可")
    enJa(FairyItem.AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY, "Passive skill is active", "パッシブスキル発動中")
    enJa(FairyItem.OVERWORLD_CONDITION_KEY, "Overworld", "地上世界")
    enJa(FairyItem.IN_AIR_CONDITION_KEY, "In the Air", "空気中")
    enJa(FairyItem.MOVEMENT_SPEED_EFFECT_KEY, "Movement Speed", "移動速度")

}

interface Fairy {
    fun getIdentifier(): Identifier
    fun getSpeedBonus(player: ServerPlayerEntity): Double?
}

interface FairyProviderItem {
    fun getFairy(): Fairy
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

class FairyItem(settings: Settings) : Item(settings), FairyProviderItem {
    companion object {
        val DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.disabled"
        val DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.duplicated"
        val UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.unavailable"
        val AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY = "item.${MirageFairy2023.modId}.fairy.description.passive_skill.available"
        val OVERWORLD_CONDITION_KEY = "item.${MirageFairy2023.modId}.passive_skill.condition.overworld"
        val IN_AIR_CONDITION_KEY = "item.${MirageFairy2023.modId}.passive_skill.condition.in_air"
        val MOVEMENT_SPEED_EFFECT_KEY = "item.${MirageFairy2023.modId}.passive_skill.effect.movement_speed"
    }

    private fun isOverworld(player: PlayerEntity) = player.world.dimension.natural

    private fun isInAir(player: PlayerEntity): Boolean {
        val blockState = player.world.getBlockState(BlockPos(player.eyePos))
        return !blockState.isOpaque && blockState.fluidState.isEmpty
    }

    override fun getFairy() = object : Fairy {
        override fun getIdentifier() = Identifier(MirageFairy2023.modId, "air")
        override fun getSpeedBonus(player: ServerPlayerEntity) = if (isOverworld(player) && isInAir(player)) 0.10 else null
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        super.appendTooltip(stack, world, tooltip, context)

        val player = MirageFairy2023.proxy?.getClientPlayer() ?: return

        val passiveFairies = player.getPassiveFairies().find { it.itemStack === stack }

        val isEnabled = passiveFairies != null
        val isDuplicated = passiveFairies != null && passiveFairies.isDuplicated
        val isOverworld = isOverworld(player)
        val isInAir = isInAir(player)
        val isAvailable = isEnabled && !isDuplicated && isOverworld && isInAir
        tooltip += text {
            when {
                isAvailable -> translate(AVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY).aqua
                isEnabled && !isDuplicated -> translate(UNAVAILABLE_PASSIVE_SKILL_DESCRIPTION_KEY).red
                isEnabled -> translate(DUPLICATED_PASSIVE_SKILL_DESCRIPTION_KEY).red
                else -> translate(DISABLED_PASSIVE_SKILL_DESCRIPTION_KEY).gray
            }
        }

        tooltip += text {
            val text = translate(MOVEMENT_SPEED_EFFECT_KEY) + " "() + (0.10 * 100 formatAs "%+.0f%%")() + " ["() + when {
                isOverworld -> translate(OVERWORLD_CONDITION_KEY)
                else -> translate(OVERWORLD_CONDITION_KEY).red
            } + ","() + when {
                isInAir -> translate(IN_AIR_CONDITION_KEY)
                else -> translate(IN_AIR_CONDITION_KEY).red
            } + "]"()
            if (isEnabled) text.aqua else text.gray
        }

    }
}
