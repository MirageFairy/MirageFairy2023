package miragefairy2023

import mirrg.kotlin.hydrogen.unit

class Slot<T : Any> : () -> T {
    private var item: T? = null
    val isEmpty get() = item == null
    fun get() = item!!
    fun set(item: T) = unit { this.item = item }
    override fun invoke() = get()
}

fun <T : Any> slotOf() = Slot<T>()

fun <T : Any> Slot<T>.getOrPut(defaultVallue: () -> T): T {
    if (this.isEmpty) this.set(defaultVallue())
    return this.get()
}

class SlotContainer<K : Any, V : Any> {
    private val map = mutableMapOf<K, Slot<V>>()
    private fun getSlot(key: K) = map.computeIfAbsent(key) { Slot<V>() }
    operator fun get(key: K) = getSlot(key).get()
    operator fun set(key: K, value: V) = getSlot(key).set(value)
}
