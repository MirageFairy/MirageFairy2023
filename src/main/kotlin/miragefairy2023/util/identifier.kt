@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.util.Identifier

fun String.toIdentifier() = Identifier(this)

infix fun String.concat(identifier: Identifier) = Identifier(identifier.namespace, this + identifier.path)
infix fun Identifier.concat(suffix: String) = Identifier(this.namespace, this.path + suffix)
