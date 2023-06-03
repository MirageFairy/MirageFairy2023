package miragefairy2023.api

import miragefairy2023.MirageFairy2023
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.SimpleRegistry

interface FairyItem {

    val fairy: Fairy

    /**
     * この妖精アイテムのベースの強さを表します。
     * スケールは妖精のレア度と同じですが、小数値を持ち、様々な補正が加算されます。
     * この値はパッシブスキルに乗じられるため、0を超えることが推奨されます。
     */
    val fairyLevel: Double

}

interface Fairy {
    val motif: Identifier
    val item: Item
    val rare: Int
}

val fairyRegistry: SimpleRegistry<Fairy> = FabricRegistryBuilder.createSimple(Fairy::class.java, Identifier(MirageFairy2023.modId, "fairy"))
    .attribute(RegistryAttribute.SYNCED)
    .buildAndRegister()
