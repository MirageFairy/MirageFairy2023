@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun jsonObjectOfNotNull(vararg pairs: Pair<String, JsonElement>?) = jsonObjectOf(pairs.filterNotNull())
fun jsonObjectOfNotNull(pairs: List<Pair<String, JsonElement>?>) = jsonObjectOf(pairs.filterNotNull())
fun jsonObjectOf(vararg pairs: Pair<String, JsonElement>) = jsonObjectOf(pairs.toList())
fun jsonObjectOf(pairs: List<Pair<String, JsonElement>>) = JsonObject().also { json ->
    pairs.forEach {
        json.add(it.first, it.second)
    }
}

fun jsonArrayOfNotNull(vararg items: JsonElement?) = jsonArrayOf(items.filterNotNull())
fun jsonArrayOfNotNull(items: List<JsonElement?>) = jsonArrayOf(items.filterNotNull())
fun jsonArrayOf(vararg items: JsonElement) = jsonArrayOf(items.toList())
fun jsonArrayOf(items: List<JsonElement>) = JsonArray().also { json ->
    items.forEach {
        json.add(it)
    }
}

val Number.jsonPrimitive get() = JsonPrimitive(this)
val String.jsonPrimitive get() = JsonPrimitive(this)
val Boolean.jsonPrimitive get() = JsonPrimitive(this)

val List<Pair<String, JsonElement>>.jsonObject get() = jsonObjectOf(this)
val Map<String, JsonElement>.jsonObject get() = jsonObjectOf(this.entries.map { it.key to it.value })
val List<JsonElement>.jsonArray get() = jsonArrayOf(this)
