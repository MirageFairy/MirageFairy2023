package miragefairy2023

class EventBus<L> {
    private val listeners = mutableListOf<L>()

    operator fun invoke(listener: L) {
        listeners += listener
    }

    fun fire(listenerConsumer: (L) -> Unit) {
        listeners.forEach {
            listenerConsumer(it)
        }
    }
}
