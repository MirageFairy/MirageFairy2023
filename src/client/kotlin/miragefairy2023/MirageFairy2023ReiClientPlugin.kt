package miragefairy2023

import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.display.DisplayCategory
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.util.EntryStacks
import miragefairy2023.modules.fairyhouse.FairyMetamorphosisAltarRecipe
import miragefairy2023.modules.fairyhouse.fairyMetamorphosisAltar
import miragefairy2023.util.text
import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.text.Text

class MirageFairy2023ReiClientPlugin : REIClientPlugin {
    override fun registerCategories(registry: CategoryRegistry) {
        registry.add(FairyMetamorphosisAltarCategory())
        registry.addWorkstations(Categories.FAIRY_METAMORPHOSIS_ALTAR, EntryStacks.of(fairyMetamorphosisAltar.blockItem.feature))
    }

    override fun registerDisplays(registry: DisplayRegistry) {
        FairyMetamorphosisAltarRecipe.RECIPES.forEach { (input, recipe2) ->
            recipe2.forEach { (category, outputList) ->
                val baseRate = when (category) {
                    FairyMetamorphosisAltarRecipe.Category.N -> 0.9
                    FairyMetamorphosisAltarRecipe.Category.R -> 0.09
                    FairyMetamorphosisAltarRecipe.Category.SR -> 0.009
                    FairyMetamorphosisAltarRecipe.Category.SSR -> 0.001
                }
                val rate = baseRate / outputList.size.toDouble()
                outputList.forEach { output ->
                    registry.add(FairyMetamorphosisAltarDisplay(EntryIngredient.of(EntryStacks.of(input)), rate, EntryIngredient.of(EntryStacks.of(output))))
                }
            }
        }
    }
}

class FairyMetamorphosisAltarCategory : DisplayCategory<FairyMetamorphosisAltarDisplay> {
    override fun getCategoryIdentifier() = Categories.FAIRY_METAMORPHOSIS_ALTAR
    override fun getIcon(): Renderer = EntryStacks.of(fairyMetamorphosisAltar.blockItem.feature)
    override fun getTitle(): Text = fairyMetamorphosisAltar.block.feature.name
    override fun setupDisplay(display: FairyMetamorphosisAltarDisplay, bounds: Rectangle): List<Widget> {
        val p = Point(bounds.centerX - 41, bounds.centerY - 13)
        return listOf(
            Widgets.createRecipeBase(bounds),
            Widgets.createArrow(p + Point(12, 4)),
            Widgets.createResultSlotBackground(p + Point(46, 5)),
            Widgets.createSlot(p + Point(-11, 5)).entries(display.inputEntries[0]).markInput(),
            Widgets.createSlot(p + Point(46, 5)).entries(display.outputEntries[0]).disableBackground().markOutput(),
            Widgets.createLabel(p + Point(87, 9), text { "${display.rate * 100 formatAs "%.2f"}%"() }).color(0xFF404040.toInt(), 0xFFBBBBBB.toInt()).noShadow(),
        )
    }

    override fun getDisplayWidth(display: FairyMetamorphosisAltarDisplay) = 150
    override fun getDisplayHeight() = 36
}

operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)