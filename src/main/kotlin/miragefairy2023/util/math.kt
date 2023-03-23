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
