package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.modules.fairy.invoke
import miragefairy2023.util.init.enJaItem
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
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.io.File
import java.io.IOException
import java.util.Optional

val debugModule = module {
    item("fairy_list_debugger", { FairyListDebuggerItem(FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider { _, _ -> 0xAA0000 }
        enJaItem({ feature }, "Fairy List Debugger", "妖精一覧デバッガー")
    }
    item("reset_telescope_mission_debugger", { ResetTelescopeMissionDebuggerItem(FabricItemSettings().group(commonItemGroup)) }) {
        onGenerateItemModels { it.register(feature, Model(Optional.of(Identifier("minecraft", "item/book")), Optional.empty())) }
        registerColorProvider { _, _ -> 0xFFC700 }
        enJaItem({ feature }, "Reset Telescope Mission Debugger", "望遠鏡クエストリセットデバッガー")
    }
}

class FairyListDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (!world.isClient) return TypedActionResult.consume(itemStack)

        val lines = FairyCard.values().map { fairyCard ->

            val passiveSkillTexts = fairyCard().passiveSkills.map { passiveSkill ->
                text {
                    val effectText = passiveSkill.effect.getText()
                    val conditionTexts = passiveSkill.conditions.map { it.getText() }
                    if (conditionTexts.isNotEmpty()) {
                        effectText + " ["() + conditionTexts.join(","()) + "]"()
                    } else {
                        effectText
                    }
                }.string
            }

            val recipeTexts = fairyCard.recipeContainer.recipes.map { it.getWikiString() }

            "|${fairyCard.jaName}|${fairyCard.rare}|${passiveSkillTexts.join("&br;")}|${recipeTexts.join("&br;")}|"
        }
        writeAction(user, "${Registry.ITEM.getId(this).path}.txt", lines.map { "$it\n" }.join(""))

        return TypedActionResult.success(itemStack)
    }
}

class ResetTelescopeMissionDebuggerItem(settings: Settings) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        if (world.isClient) return TypedActionResult.consume(itemStack)
        user as ServerPlayerEntity

        user.lastTelescopeUseTime = null
        user.sendMessage(text { "Reset telescope mission "() }, false)

        return TypedActionResult.success(itemStack)
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
