package miragefairy2023

import me.shedaniel.rei.api.common.category.CategoryIdentifier
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.display.basic.BasicDisplay
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import miragefairy2023.util.double
import miragefairy2023.util.get
import miragefairy2023.util.wrapper

object Categories {
    val FAIRY_METAMORPHOSIS_ALTAR: CategoryIdentifier<FairyMetamorphosisAltarDisplay> = CategoryIdentifier.of(MirageFairy2023.modId, "plugins/fairy_metamorphosis_altar")
}

class MirageFairy2023ReiServerPlugin : REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(Categories.FAIRY_METAMORPHOSIS_ALTAR, FairyMetamorphosisAltarDisplay.serializer())
    }
}

class FairyMetamorphosisAltarDisplay(
    input: EntryIngredient,
    val rate: Double,
    output: EntryIngredient,
) : BasicDisplay(listOf(input), listOf(output)) {
    companion object {
        fun serializer(): Serializer<FairyMetamorphosisAltarDisplay> = Serializer.of({ inputs, outputs, _, tag ->
            FairyMetamorphosisAltarDisplay(
                inputs[0],
                tag.wrapper["rate"].double.get() ?: 0.0,
                outputs[0],
            )
        }, { display, tag ->
            tag.wrapper["rate"].double.set(display.rate)
        })
    }

    override fun getCategoryIdentifier() = Categories.FAIRY_METAMORPHOSIS_ALTAR
}
