package miragefairy2023.core.init

class InitializationScope(val modId: String) {
    val itemRegistration = EventBus<() -> Unit>()
    val recipeRegistration = EventBus<() -> Unit>()
}
