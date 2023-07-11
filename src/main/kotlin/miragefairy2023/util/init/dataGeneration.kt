@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.init

inline fun <T> configure(receiver: T, initializer: T.() -> Unit) = receiver.apply(initializer)
