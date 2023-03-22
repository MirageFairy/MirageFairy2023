@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

fun jsonObjectOf(vararg pairs: Pair<String, JsonElement>) = jsonObjectOf(pairs.toList())
fun jsonObjectOf(pairs: List<Pair<String, JsonElement>>) = JsonObject().also { json ->
    pairs.forEach {
        json.add(it.first, it.second)
    }
}

val String.jsonPrimitive get() = JsonPrimitive(this)
