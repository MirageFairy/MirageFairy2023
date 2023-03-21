package miragefairy2023.core.init

import mirrg.kotlin.hydrogen.unit

class Slot<T : Any> : () -> T {
    lateinit var item: T
    override fun invoke() = item
}

class SlotContainer<K : Any, V : Any> {
    private val map = mutableMapOf<K, Slot<V>>()
    private fun getSlot(key: K) = map.computeIfAbsent(key) { Slot<V>() }
    operator fun get(key: K) = getSlot(key).item
    operator fun set(key: K, value: V) = unit { getSlot(key).item = value }
}
