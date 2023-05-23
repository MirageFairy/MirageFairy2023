package miragefairy2023.modules.fairyhouse

import miragefairy2023.InitializationScope
import miragefairy2023.MirageFairy2023
import miragefairy2023.RenderingProxy
import miragefairy2023.RenderingProxyBlockEntity
import miragefairy2023.modules.DemonParticleTypeCard
import miragefairy2023.modules.commonItemGroup
import miragefairy2023.util.gray
import miragefairy2023.util.init.block
import miragefairy2023.util.init.blockEntity
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.enJaBlock
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.item
import miragefairy2023.util.text
import miragefairy2023.util.yellow
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.BlockTags
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

fun <B, BE> InitializationScope.registerFairyHouse(card: FairyHouseCard<B, BE>) where B : Block, BE : BlockEntity, BE : RenderingProxyBlockEntity {
    card.block = block(card.path, { card.blockCreator(FabricBlockSettings.of(card.material).sounds(card.soundGroup).requiresTool().strength(2.0F).nonOpaque()) }) {

        // レンダリング
        generateHorizontalFacingBlockState()
        onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(feature) }

        // 翻訳
        enJaBlock({ feature }, card.enName, card.jaName)
        enJa({ "${feature.translationKey}.poem" }, card.enPoem, card.jaPoem)
        enJa({ "${feature.translationKey}.description" }, card.enDescription, card.jaDescription)

        // レシピ
        onGenerateBlockTags { it(BlockTags.PICKAXE_MINEABLE).add(feature) }
        if (card.needsToolTag != null) onGenerateBlockTags { it(card.needsToolTag).add(feature) }
        generateDefaultBlockLootTable()

    }
    card.blockEntityType = blockEntity(card.path, card.blockEntityCreator, { card.block.feature }) {
        onInitializeClient { MirageFairy2023.clientProxy!!.registerRenderingProxyBlockEntityRendererFactory(feature) }
    }
    card.blockItem = item(card.path, {
        object : BlockItem(card.block.feature, FabricItemSettings().group(commonItemGroup)) {
            override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
                super.appendTooltip(stack, world, tooltip, context)
                tooltip += text { translate("$translationKey.poem").gray }
                tooltip += text { translate("$translationKey.description").yellow }
            }
        }
    })
}

fun ServerWorld.spawnCraftingCompletionParticles(pos: Vec3d) {
    this.spawnParticles(DemonParticleTypeCard.DESCENDING_MAGIC.particleType, pos.x, pos.y, pos.z, 5, 0.0, 0.0, 0.0, 0.02)
}

fun RenderingProxy.renderItemStack(itemStack: ItemStack, dotX: Double, dotY: Double, dotZ: Double, scale: Float = 1.0F, rotate: Float = 0.0F) {
    this.stack {
        this.translate(dotX / 16.0, dotY / 16.0, dotZ / 16.0)
        this.scale(scale, scale, scale)
        this.rotateY(rotate)
        this.renderItem(itemStack)
    }
}
