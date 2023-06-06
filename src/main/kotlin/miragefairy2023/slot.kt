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
