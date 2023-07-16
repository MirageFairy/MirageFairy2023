package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.addAvailableParticle
import miragefairy2023.util.identifier
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.enJa
import miragefairy2023.util.init.generateDefaultBlockLootTable
import miragefairy2023.util.init.generateHorizontalFacingBlockState
import miragefairy2023.util.init.group
import miragefairy2023.util.init.registerFuel
import miragefairy2023.util.lib.InstrumentBlock
import miragefairy2023.util.obtain
import miragefairy2023.util.text
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

object FairyMailbox {
    val identifier = Identifier(MirageFairy2023.modId, "fairy_mailbox")
    val block = FairyMailboxBlock(FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).strength(2.5F).nonOpaque())
    val item = BlockItem(block, FabricItemSettings().group(commonItemGroup))
}

val fairyMailboxModule = module {

    // 登録
    Registry.register(Registry.BLOCK, FairyMailbox.identifier, FairyMailbox.block)
    Registry.register(Registry.ITEM, FairyMailbox.identifier, FairyMailbox.item)


    // 見た目

    // モデル
    generateHorizontalFacingBlockState(FairyMailbox.block, FairyMailbox.identifier)
    onInitializeClient { MirageFairy2023.clientProxy!!.registerCutoutBlockRenderLayer(FairyMailbox.block) }

    // 翻訳
    enJa(FairyMailbox.block, "Fairy Mailbox", "妖精の郵便受け")
    val poemList = listOf(
        Poem("A switch that reveals a hidden fairy", "魂の還る場所。"),
        Description("Use to receive item being transported", "使用時、転送中のアイテムを受け取る"),
    )
    generatePoemList(FairyMailbox.item, poemList)
    onRegisterItems { registerPoemList(FairyMailbox.item, poemList) }


    // 性質
    onGenerateBlockTags { it(BlockTags.AXE_MINEABLE).add(FairyMailbox.block) }


    // レシピ
    generateDefaultBlockLootTable(FairyMailbox.block)
    onGenerateRecipes {
        ShapedRecipeJsonBuilder
            .create(FairyMailbox.item)
            .pattern(" W ")
            .pattern("WDW")
            .pattern(" C ")
            .input('W', ItemTags.LOGS)
            .input('D', MirageFlourCard.TINY_MIRAGE_FLOUR.item.feature)
            .input('C', Items.CHEST)
            .criterion(MirageFlourCard.TINY_MIRAGE_FLOUR.item.feature)
            .group(FairyMailbox.item)
            .offerTo(it, FairyMailbox.item.identifier)
    }
    registerFuel(FairyMailbox.item, 300)

}

class FairyMailboxBlock(settings: Settings) : InstrumentBlock(settings) {
    companion object {
        private val FACING_TO_SHAPE: Map<Direction, VoxelShape> = mapOf(
            Direction.NORTH to createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            Direction.SOUTH to createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            Direction.WEST to createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
            Direction.EAST to createCuboidShape(2.0, 0.0, 2.0, 14.0, 15.0, 14.0),
        )
    }

    override fun getPlacementDirection(playerDirection: Direction): Direction = playerDirection.opposite

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = FACING_TO_SHAPE[getFacing(state)]


    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (world.isClient) return ActionResult.SUCCESS
        player as ServerPlayerEntity

        // アイテムがあるならランダムに1個取る
        val itemStack = player.itemTransportation.removeRandom()
        if (itemStack != null) {
            player.obtain(itemStack)
            player.itemTransportation.sync()
        }

        // エフェクト
        player.sendMessage(text { ITEM_TRANSPORTATION_COUNT_KEY(player.itemTransportation.size(), ITEM_TRANSPORTATION_LIMIT) }, true)

        return ActionResult.CONSUME
    }

    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val player = MirageFairy2023.clientProxy?.getClientPlayer() ?: return

        if (player.itemTransportation.isEmpty()) return // 転送中のアイテムは無い

        addAvailableParticle(world, pos)
    }

}
