@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

enum class Symbol(val general: String, val uniformed: String) {
    STAR("★", "\uE600"),
    HEART("❤", "\uE601"),
    FOOD("🍖", "\uE602"),
    LEVEL("Lv", "\uE603"),
    LUCK("🍀", "\uE604"),
    LIGHT("💡", "\uE605"),
    UP("↑", "\uE606"),
    DOWN("↓", "\uE607"),
    ;

    override fun toString() = uniformed
}
