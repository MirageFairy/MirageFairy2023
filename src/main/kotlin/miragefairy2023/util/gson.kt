@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun jsonObjectOf(vararg pairs: Pair<String, JsonElement>) = jsonObjectOf(pairs.toList())
fun jsonObjectOf(pairs: List<Pair<String, JsonElement>>) = JsonObject().also { json ->
    pairs.forEach {
        json.add(it.first, it.second)
    }
}

fun jsonArrayOf(vararg items: JsonElement) = jsonArrayOf(items.toList())
fun jsonArrayOf(items: List<JsonElement>) = JsonArray().also { json ->
    items.forEach {
        json.add(it)
    }
}

val Number.jsonPrimitive get() = JsonPrimitive(this)
val String.jsonPrimitive get() = JsonPrimitive(this)
val Boolean.jsonPrimitive get() = JsonPrimitive(this)
