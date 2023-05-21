package miragefairy2023.modules.fairy

import miragefairy2023.MirageFairy2023
import miragefairy2023.api.Fairy
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

fun getRareColor(rare: Int): Formatting = when (rare) {
    0 -> Formatting.AQUA
    1 -> Formatting.GRAY
    2 -> Formatting.WHITE
    3 -> Formatting.GREEN
    4 -> Formatting.DARK_GREEN
    5 -> Formatting.YELLOW
    6 -> Formatting.GOLD
    7 -> Formatting.RED
    8 -> Formatting.DARK_RED
    9 -> Formatting.BLUE
    10 -> Formatting.DARK_BLUE
    11 -> Formatting.LIGHT_PURPLE
    12 -> Formatting.DARK_PURPLE
    else -> Formatting.DARK_AQUA
}

fun getRankRgb(rank: Int) = when (rank) {
    1 -> 0xAA0000 // red
    2 -> 0x0000FF // blue
    3 -> 0x009200 // green
    4 -> 0xE5E500 // yellow
    5 -> 0x131313 // black
    6 -> 0xFFFFFF // white
    7 -> 0x65FFFF // cyan
    8 -> 0xFF8E00 // orange
    9 -> 0xBD41FF // purple
    else -> 0xFF00FF
}

// TODO respect
val Fairy.isLiquidFairy
    get() = when (this.motif) {
        Identifier(MirageFairy2023.modId, "water") -> true
        Identifier(MirageFairy2023.modId, "lava") -> true
        else -> false
    }
