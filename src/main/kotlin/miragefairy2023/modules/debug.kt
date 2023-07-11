package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.modules.fairy.invoke
import miragefairy2023.modules.toolitem.foundFairies
import miragefairy2023.util.Symbol
import miragefairy2023.util.datagen.enJaItem
import miragefairy2023.util.identifier
import miragefairy2023.util.init.item
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
import net.minecraft.world.World
import java.io.File
import java.io.IOException
import java.util.Optional

val debugModule = module {
    item("fairy_list_debugger", { FairyListDebuggerItem(FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider({ feature }) { _, _ -> 0xAA0000 }
        enJaItem({ feature }, "Fairy List Debugger", "妖精一覧デバッガー")
    }
    item("reset_telescope_mission_debugger", { ResetTelescopeMissionDebuggerItem(FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider({ feature }) { _, _ -> 0xFFC700 }
        enJaItem({ feature }, "Reset Telescope Mission Debugger", "望遠鏡ミッションリセットデバッガー")
    }
    item("reset_fairy_dream_debugger", { ResetFairyDreamDebuggerItem(FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider({ feature }) { _, _ -> 0x00FFC3 }
        enJaItem({ feature }, "Reset Fairy Dream Debugger", "妖精の夢リセットデバッガー")
    }
}

class FairyListDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (!world.isClient) return TypedActionResult.consume(itemStack)

        val lines = FairyCard.values().map { fairyCard ->

            val item = fairyCard()
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
