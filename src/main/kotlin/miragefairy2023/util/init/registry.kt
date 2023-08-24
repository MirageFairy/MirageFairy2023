package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

fun <T> InitializationScope.register(registry: Registry<T>, identifier: Identifier, entry: T) = onInitialize {
    Registry.register(registry, identifier, entry)
}
