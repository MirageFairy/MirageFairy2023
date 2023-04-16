package miragefairy2023.modules.fairy

import net.minecraft.util.Formatting

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
    1 -> 0x6E6E6E
    2 -> 0xAA0000
    3 -> 0x0000DF
    4 -> 0x009200
    5 -> 0xD5D500
    6 -> 0x131313
    7 -> 0xFFFFFF
    8 -> 0x00FFFF
    9 -> 0x9700D5
    else -> 0xFF00FF
}
