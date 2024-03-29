package miragefairy2023

import dev.architectury.fluid.FluidStack
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.gui.AbstractRenderer
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Tooltip
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.client.util.ClientEntryStacks
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.entry.EntryStack
import me.shedaniel.rei.api.common.util.EntryStacks
import miragefairy2023.api.Fairy
import miragefairy2023.modules.COLLECTED_KEY
import miragefairy2023.modules.UNCOLLECTED_KEY
import miragefairy2023.modules.fairy.BLOCK_FAIRY_RELATION_LIST
import miragefairy2023.modules.fairy.ENTITY_TYPE_FAIRY_RELATION_LIST
import miragefairy2023.modules.fairyhouse.FairyMetamorphosisAltarRecipe
import miragefairy2023.modules.fairyhouse.fairyMetamorphosisAltar
import miragefairy2023.modules.toolitem.DREAM_CATCHERS
import miragefairy2023.modules.foundFairies
import miragefairy2023.util.removeTrailingZeros
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.block.Block
import net.minecraft.block.FluidBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.SpawnEggItem
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry


class MirageFairy2023ReiClientPlugin : REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry) {

        registry.add(FairyMetamorphosisAltarCategory())
        registry.addWorkstations(Categories.FAIRY_METAMORPHOSIS_ALTAR, fairyMetamorphosisAltar.item.toEntryStack())

        registry.add(BlockFairyRelationCategory())
        Registry.ITEM.getEntryList(DREAM_CATCHERS).orElse(null)?.forEach {
            registry.addWorkstations(Categories.BLOCK_FAIRY_RELATION, it.value().toEntryStack())
        }

        registry.add(EntityTypeFairyRelationCategory())
        Registry.ITEM.getEntryList(DREAM_CATCHERS).orElse(null)?.forEach {
            registry.addWorkstations(Categories.ENTITY_TYPE_FAIRY_RELATION, it.value().toEntryStack())
        }

    }

    override fun registerDisplays(registry: DisplayRegistry) {

        FairyMetamorphosisAltarRecipe.RECIPES.keys.forEach { input ->
            val chanceTable = FairyMetamorphosisAltarRecipe.getChanceTable(input, 1.0) ?: return@forEach
            chanceTable.forEach { (chance, output) ->
                registry.add(FairyMetamorphosisAltarDisplay(EntryIngredient.of(input.toEntryStack()), chance, EntryIngredient.of(output.toEntryStack())))
            }
        }

        BLOCK_FAIRY_RELATION_LIST.forEach { relation ->
            registry.add(BlockFairyRelationDisplay(EntryIngredient.of(relation.block.toEntryStack()), EntryIngredient.of(relation.fairy.toEntryStack()), relation.fairy.motif))
        }

        ENTITY_TYPE_FAIRY_RELATION_LIST.forEach { relation ->
            registry.add(EntityTypeFairyRelationDisplay(EntryIngredient.of(relation.entityType.toEntryStack()), EntryIngredient.of(relation.fairy.toEntryStack()), relation.fairy.motif))
        }

    }
}


class FairyMetamorphosisAltarCategory : DisplayCategory<FairyMetamorphosisAltarDisplay> {
    override fun getCategoryIdentifier() = Categories.FAIRY_METAMORPHOSIS_ALTAR
    override fun getIcon(): Renderer = fairyMetamorphosisAltar.item.toEntryStack()
    override fun getTitle(): Text = fairyMetamorphosisAltar.block.name
    override fun setupDisplay(display: FairyMetamorphosisAltarDisplay, bounds: Rectangle): List<Widget> {
        val p = Point(bounds.centerX - 41, bounds.centerY - 13)
        val rateText = (display.rate * 100 formatAs "%.3f").removeTrailingZeros() + "%"
        return listOf(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(p + Point(7, 4)),
            Widgets.createResultSlotBackground(p + Point(41, 5)),
            Widgets.createSlot(p + Point(-16, 5)).entries(display.inputEntries[0]).markInput(),
            Widgets.createSlot(p + Point(41, 5)).entries(display.outputEntries[0]).disableBackground().markOutput(),
            Widgets.createLabel(p + Point(88, 9), text { rateText() }).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow(),
        )
    }

    override fun getDisplayWidth(display: FairyMetamorphosisAltarDisplay) = 150
    override fun getDisplayHeight() = 36
}

class BlockFairyRelationCategory : DisplayCategory<BlockFairyRelationDisplay> {
    override fun getCategoryIdentifier() = Categories.BLOCK_FAIRY_RELATION
    override fun getIcon(): Renderer = Items.IRON_BLOCK.toEntryStack()
    override fun getTitle() = text { BLOCK_FAIRY_RELATION_KEY() }
    override fun setupDisplay(display: BlockFairyRelationDisplay, bounds: Rectangle): List<Widget> {
        val p = Point(bounds.centerX - 21 - 20, bounds.centerY - 13)
        val collectedLabel = if (MinecraftClient.getInstance()?.player?.foundFairies?.get(display.motif) == true) {
            Widgets.createLabel(p + Point(88, 9), text { COLLECTED_KEY() }).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow()
        } else {
            Widgets.createLabel(p + Point(88, 9), text { UNCOLLECTED_KEY() }).color(0xFFFF0000.toInt(), 0xFFFF0000.toInt()).noShadow()
        }
        return listOf(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(p + Point(7, 4)),
            Widgets.createResultSlotBackground(p + Point(41, 5)),
            Widgets.createSlot(p + Point(-16, 5)).entries(display.inputEntries[0]).markInput(),
            Widgets.createSlot(p + Point(41, 5)).entries(display.outputEntries[0]).disableBackground().markOutput(),
            collectedLabel,
        )
    }

    override fun getDisplayWidth(display: BlockFairyRelationDisplay) = 150
    override fun getDisplayHeight() = 36
}

class EntityTypeFairyRelationCategory : DisplayCategory<EntityTypeFairyRelationDisplay> {
    override fun getCategoryIdentifier() = Categories.ENTITY_TYPE_FAIRY_RELATION
    override fun getIcon(): Renderer = Items.BONE.toEntryStack()
    override fun getTitle() = text { ENTITY_TYPE_FAIRY_RELATION_KEY() }
    override fun setupDisplay(display: EntityTypeFairyRelationDisplay, bounds: Rectangle): List<Widget> {
        val p = Point(bounds.centerX - 21 - 20, bounds.centerY - 13)
        val collectedLabel = if (MinecraftClient.getInstance()?.player?.foundFairies?.get(display.motif) == true) {
            Widgets.createLabel(p + Point(88, 9), text { COLLECTED_KEY() }).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow()
        } else {
            Widgets.createLabel(p + Point(88, 9), text { UNCOLLECTED_KEY() }).color(0xFFFF0000.toInt(), 0xFFFF0000.toInt()).noShadow()
        }
        return listOf(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(p + Point(7, 4)),
            Widgets.createResultSlotBackground(p + Point(41, 5)),
            Widgets.createSlot(p + Point(-16, 5)).entries(display.inputEntries[0]).markInput(),
            Widgets.createSlot(p + Point(41, 5)).entries(display.outputEntries[0]).disableBackground().markOutput(),
            collectedLabel,
        )
    }

    override fun getDisplayWidth(display: EntityTypeFairyRelationDisplay) = 150
    override fun getDisplayHeight() = 36
}


operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)


fun Item.toEntryStack(): EntryStack<ItemStack> = EntryStacks.of(this)

fun ItemStack.toEntryStack(): EntryStack<ItemStack> = EntryStacks.of(this)

fun Block.toEntryStack(): EntryStack<*> {

    // 流体ブロックなら流体から
    if (this is FluidBlock) return EntryStacks.of(this.defaultState.fluidState.fluid)

    // ピックが可能ならそれ
    val pickedItemStack = try {
        this.getPickStack(null, null, this.defaultState)
    } catch (_: Exception) {
        null
    }
    if (pickedItemStack != null && !pickedItemStack.isEmpty) return EntryStacks.of(pickedItemStack)

    // アイテムがあるならそれ
    val item = this.asItem()
    if (item != Items.AIR) return EntryStacks.of(item)

    // 何も無いので「？」
    return this.name.toEntryStack()
}

fun Fluid.toEntryStack(): EntryStack<FluidStack> = EntryStacks.of(this)

fun FluidStack.toEntryStack(): EntryStack<FluidStack> = EntryStacks.of(this)

fun EntityType<*>.toEntryStack(): EntryStack<*> {
    val item = SpawnEggItem.forEntity(this)
    if (item != null) return item.toEntryStack()
    return this.name.toEntryStack()
}

fun Fairy.toEntryStack() = this.item.toEntryStack()

fun String.toEntryStack() = text { this@toEntryStack() }.toEntryStack()

fun Text.toEntryStack(): EntryStack<*> {
    return ClientEntryStacks.of(object : AbstractRenderer() {
        override fun render(matrices: MatrixStack, bounds: Rectangle, mouseX: Int, mouseY: Int, delta: Float) {
            val string = "?"
            val textRenderer = MinecraftClient.getInstance().textRenderer
            val width = textRenderer.getWidth(string)
            val height = textRenderer.fontHeight
            textRenderer.draw(matrices, string, bounds.centerX - width / 2F + 0.2F, bounds.centerY - height / 2F + 1F, if (REIRuntime.getInstance().isDarkThemeEnabled) 0xFFBBBBBB.toInt() else 0xFF404040.toInt())
        }

        override fun getTooltip(context: TooltipContext) = Tooltip.create(context.point, this@toEntryStack)
    })
}
