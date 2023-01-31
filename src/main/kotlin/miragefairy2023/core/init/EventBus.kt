package miragefairy2023.core.init

class EventBus<L> {
    private val listeners = mutableListOf<L>()
    operator fun plusAssign(listener: L) {
        listeners += listener
    }

    fun fire(listenerConsumer: (L) -> Unit) {
        listeners.forEach {
            listenerConsumer(it)
        }
    }
}
