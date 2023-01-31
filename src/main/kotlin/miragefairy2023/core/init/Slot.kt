package miragefairy2023.core.init

class Slot<T : Any> : () -> T {
    lateinit var item: T
    override fun invoke() = item
}
