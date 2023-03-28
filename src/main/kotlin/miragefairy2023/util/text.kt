@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.text.Text
import net.minecraft.util.Formatting

val Text.black: Text get() = Text.empty().append(this).formatted(Formatting.BLACK)
val Text.darkBlue: Text get() = Text.empty().append(this).formatted(Formatting.DARK_BLUE)
val Text.darkGreen: Text get() = Text.empty().append(this).formatted(Formatting.DARK_GREEN)
val Text.darkAqua: Text get() = Text.empty().append(this).formatted(Formatting.DARK_AQUA)
val Text.darkRed: Text get() = Text.empty().append(this).formatted(Formatting.DARK_RED)
val Text.darkPurple: Text get() = Text.empty().append(this).formatted(Formatting.DARK_PURPLE)
val Text.gold: Text get() = Text.empty().append(this).formatted(Formatting.GOLD)
val Text.gray: Text get() = Text.empty().append(this).formatted(Formatting.GRAY)
val Text.darkGray: Text get() = Text.empty().append(this).formatted(Formatting.DARK_GRAY)
val Text.blue: Text get() = Text.empty().append(this).formatted(Formatting.BLUE)
val Text.green: Text get() = Text.empty().append(this).formatted(Formatting.GREEN)
val Text.aqua: Text get() = Text.empty().append(this).formatted(Formatting.AQUA)
val Text.red: Text get() = Text.empty().append(this).formatted(Formatting.RED)
val Text.lightPurple: Text get() = Text.empty().append(this).formatted(Formatting.LIGHT_PURPLE)
val Text.yellow: Text get() = Text.empty().append(this).formatted(Formatting.YELLOW)
val Text.white: Text get() = Text.empty().append(this).formatted(Formatting.WHITE)
val Text.obfuscated: Text get() = Text.empty().append(this).formatted(Formatting.OBFUSCATED)
val Text.bold: Text get() = Text.empty().append(this).formatted(Formatting.BOLD)
val Text.strikethrough: Text get() = Text.empty().append(this).formatted(Formatting.STRIKETHROUGH)
val Text.underline: Text get() = Text.empty().append(this).formatted(Formatting.UNDERLINE)
val Text.italic: Text get() = Text.empty().append(this).formatted(Formatting.ITALIC)


object TextScope {
    operator fun String.invoke(): Text = Text.of(this)
    operator fun Translation.invoke(): Text = Text.translatable(this.key)
    fun translate(key: String): Text = Text.translatable(key)
    fun translate(key: String, vararg args: Any?): Text = Text.translatable(key, *args)
    operator fun Text.plus(text: Text): Text = Text.empty().append(this).append(text)
}

inline fun text(block: TextScope.() -> Text) = block(TextScope)

fun Iterable<Text>.join(separator: Text): Text {
    val result = Text.empty()
    this.forEachIndexed { index, text ->
        if (index != 0) result.append(separator)
        result.append(text)
    }
    return result
}
