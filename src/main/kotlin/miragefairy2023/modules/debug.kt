package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.util.Symbol
import miragefairy2023.util.identifier
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.register
import miragefairy2023.util.init.registerColorProvider
import miragefairy2023.util.join
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.join
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.data.client.Model
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.io.File
import java.io.IOException
import java.util.Optional

enum class DebuggerItemCard(
    val path: String,
    val en: String,
    val ja: String,
    itemCreator: (Item.Settings) -> Item,
    val color: Int,
) {
    FAIRY_LIST_DEBUGGER(
        "fairy_list_debugger", "Fairy List Debugger", "妖精一覧デバッガー",
        { FairyListDebuggerItem(it) }, 0xAA0000,
    ),
    RESET_TELESCOPE_MISSION_DEBUGGER(
        "reset_telescope_mission_debugger", "Reset Telescope Mission Debugger", "望遠鏡ミッションリセットデバッガー",
        { ResetTelescopeMissionDebuggerItem(it) }, 0xFFC700,
    ),
    RESET_FAIRY_DREAM_DEBUGGER(
        "reset_fairy_dream_debugger", "Reset Fairy Dream Debugger", "妖精の夢リセットデバッガー",
        { ResetFairyDreamDebuggerItem(it) }, 0x00FFC3,
    ),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path)
    val item = itemCreator(FabricItemSettings().group(commonItemGroup))
}

val debugModule = module {
    DebuggerItemCard.values().forEach { card ->

        // 登録
        register(Registry.ITEM, card.identifier, card.item)

        // モデル
        onGenerateItemModels { it.register(card.item, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider(card.item) { _, _ -> card.color }

        // 翻訳
        enJa(card.item, card.en, card.ja)

    }
}

class FairyListDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (!world.isClient) return TypedActionResult.consume(itemStack)

        val lines = FairyCard.values().map { fairyCard ->

            val item = fairyCard[1].item
            val passiveSkillTexts = item.fairyCard.passiveSkills.map { passiveSkill ->
                val string: String = text {
                    val effectText = passiveSkill.effect.getText(item.passiveSkillProvider.mana / 10.0, item.passiveSkillProvider.mana / 10.0)
                    val conditionTexts = passiveSkill.conditions.map { it.getText() }
                    if (conditionTexts.isNotEmpty()) {
                        effectText + " ["() + conditionTexts.join(","()) + "]"()
                    } else {
                        effectText
                    }
                }.string
                Symbol.values().fold(string) { it, symbol -> it.replace(symbol.uniformed, symbol.general) }
            }

            val recipeTexts = fairyCard.fairyRecipes.recipes.map { it.getWikiString() }

            "|${fairyCard.jaName}|${fairyCard.rare}|${passiveSkillTexts.join("&br;")}|${recipeTexts.join("&br;")}|"
        }
        writeAction(user, "${identifier.path}.txt", lines.map { "$it\n" }.join(""))

        return TypedActionResult.success(itemStack)
    }
}

class ResetTelescopeMissionDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.success(itemStack)
        user as ServerPlayerEntity

        user.lastTelescopeUseTimeProperty.set(null)
        user.syncCustomData()
        user.sendMessage(text { "Reset telescope mission"() }, false)

        user.world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.5F, 1.0F)

        return TypedActionResult.consume(itemStack)
    }
}

class ResetFairyDreamDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.success(itemStack)
        user as ServerPlayerEntity

        user.foundFairies.reset()
        user.syncCustomData()
        user.sendMessage(text { "Reset fairy dreams"() }, false)

        user.world.playSound(null, user.x, user.y, user.z, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 0.5F, 1.0F)

        return TypedActionResult.consume(itemStack)
    }
}

private fun writeAction(player: PlayerEntity, fileName: String, text: String) {
    val file = File("debug").resolve(fileName)
    player.sendMessage(text { "Saved to "() + file() }, false)
    when {
        file.parentFile.isDirectory -> Unit
        file.parentFile.exists() -> throw IOException("Failed to create directory: $file")
        !file.parentFile.mkdirs() -> throw IOException("Failed to create directory: $file")
    }
    file.writeText(text)
}
