@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import net.minecraft.text.Text
import net.minecraft.util.Formatting

fun Text.formatted(formatting: Formatting): Text = Text.empty().append(this).formatted(formatting)
val Text.black get() = this.formatted(Formatting.BLACK)
val Text.darkBlue get() = this.formatted(Formatting.DARK_BLUE)
val Text.darkGreen get() = this.formatted(Formatting.DARK_GREEN)
val Text.darkAqua get() = this.formatted(Formatting.DARK_AQUA)
val Text.darkRed get() = this.formatted(Formatting.DARK_RED)
val Text.darkPurple get() = this.formatted(Formatting.DARK_PURPLE)
val Text.gold get() = this.formatted(Formatting.GOLD)
val Text.gray get() = this.formatted(Formatting.GRAY)
val Text.darkGray get() = this.formatted(Formatting.DARK_GRAY)
val Text.blue get() = this.formatted(Formatting.BLUE)
val Text.green get() = this.formatted(Formatting.GREEN)
val Text.aqua get() = this.formatted(Formatting.AQUA)
val Text.red get() = this.formatted(Formatting.RED)
val Text.lightPurple get() = this.formatted(Formatting.LIGHT_PURPLE)
val Text.yellow get() = this.formatted(Formatting.YELLOW)
val Text.white get() = this.formatted(Formatting.WHITE)
val Text.obfuscated get() = this.formatted(Formatting.OBFUSCATED)
val Text.bold get() = this.formatted(Formatting.BOLD)
val Text.strikethrough get() = this.formatted(Formatting.STRIKETHROUGH)
val Text.underline get() = this.formatted(Formatting.UNDERLINE)
val Text.italic get() = this.formatted(Formatting.ITALIC)


object TextScope {
    operator fun String.invoke(): Text = Text.of(this)
    operator fun Translation.invoke(): Text = Text.translatable(this.key)
    operator fun Translation.invoke(vararg args: Any?): Text = Text.translatable(this.key, *args)
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
