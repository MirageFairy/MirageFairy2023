@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import miragefairy2023.util.init.Translation
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import java.io.File

inline fun text(block: TextScope.() -> Text) = block(TextScope)

object TextScope {
    operator fun String.invoke(): Text = Text.of(this)
    operator fun Translation.invoke(): Text = Text.translatable(this.key)
    operator fun Translation.invoke(vararg args: Any?): Text = Text.translatable(this.key, *args)
    fun translate(key: String): Text = Text.translatable(key)
    fun translate(key: String, vararg args: Any?): Text = Text.translatable(key, *args)
    operator fun Text.plus(text: Text): Text = Text.empty().append(this).append(text)
    operator fun File.invoke() = Text.literal(name).styled {
        it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, text { absoluteFile.canonicalPath() }))
        it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_FILE, absoluteFile.canonicalPath))
    }.underline
}
