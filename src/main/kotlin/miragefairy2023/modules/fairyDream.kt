package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.util.get
import miragefairy2023.util.int
import miragefairy2023.util.map
import miragefairy2023.util.string
import miragefairy2023.util.wrapper
import mirrg.kotlin.hydrogen.castOrNull
import mirrg.kotlin.hydrogen.or
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.util.Identifier

val PlayerEntity.foundFairies get() = FoundFairies(this)

class FoundFairies(private val player: PlayerEntity) {
    fun reset() = player.customData.wrapper[MirageFairy2023.modId]["found_motifs"].set(null)
    fun add(motif: Identifier) = player.customData.wrapper[MirageFairy2023.modId]["found_motifs"][motif.string].int.set(1)
    operator fun get(motif: Identifier): Boolean = (player.customData.wrapper[MirageFairy2023.modId]["found_motifs"][motif.string].int.get() ?: 0) != 0
    fun getList() = player.customData.wrapper[MirageFairy2023.modId]["found_motifs"].map.get().or { mapOf() }.entries
        .filter { it.value.castOrNull<AbstractNbtNumber>()?.intValue() != 0 }
        .map { Identifier(it.key) }
        .toSet()
}
