@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import mirrg.kotlin.hydrogen.formatAs
import net.minecraft.util.math.random.Random

class Chance<out T : Any>(val weight: Double, val item: T) {
    override fun toString() = "${weight formatAs "%8.4f"}: $item"
}

val <T : Any> List<Chance<T>>.totalWeight get() = this.sumOf { it.weight }

/** @param randomValue 0以上1未満の値 */
fun <T : Any> List<Chance<T>>.draw(randomValue: Double): T? {
    if (this.isEmpty()) return null

    var w = randomValue * totalWeight // 0 <= w < totalWeight
    this.forEach { item ->
        w -= item.weight
        if (w < 0) return item.item
    }
    return this.last().item
}

fun <T : Any> List<Chance<T>>.draw(random: Random) = draw(random.nextDouble())


/** 同一キーのエントリの重みを加算することによってキーをユニークにします。 */
fun <T : Any> List<Chance<T>>.distinct(equals: (T, T) -> Boolean): List<Chance<T>> {
    class Slot(val item: T) {
        override fun hashCode() = 0

        override fun equals(other: Any?): Boolean {
            if (this === other) return true // 相手が自分自身なら一致
            if (other == null) return false // 相手が無なら不一致

            // 型チェック
            if (javaClass != other.javaClass) return false
            @Suppress("UNCHECKED_CAST")
            other as Slot

            return equals(item, other.item)
        }
    }

    val map = mutableMapOf<Slot, Double>()
    this.forEach { item ->
        map[Slot(item.item)] = (map[Slot(item.item)] ?: 0.0) + item.weight
    }
    return map.entries.map { Chance(it.value, it.key.item) }
}
