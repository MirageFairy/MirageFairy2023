@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.util.math.random.Random
import kotlin.math.floor

/** 期待値がdになるように整数の乱数を生成します。 */
fun Random.randomInt(d: Double): Int {
    val i = floor(d).toInt()
    val mod = d - i
    return if (this.nextDouble() < mod) i + 1 else i
}

fun Int.toRoman() = when (this) {
    1 -> "I"
    2 -> "II"
    3 -> "III"
    4 -> "IV"
    5 -> "V"
    6 -> "VI"
    7 -> "VII"
    8 -> "VIII"
    9 -> "IX"
    else -> "$this"
}

fun String.removeTrailingZeros() = this.replace("""\.?0+$""".toRegex(), "")
