@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

inline fun <reified O : Any> Any.castOr(block: () -> O) = this as? O ?: block()
