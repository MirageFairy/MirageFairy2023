@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util.datagen

import com.google.gson.JsonElement
import net.minecraft.data.client.Model
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.function.BiConsumer
import java.util.function.Supplier

fun Model(creator: (TextureMap) -> JsonElement): Model = object : Model(Optional.empty(), Optional.empty()) {
    override fun upload(id: Identifier, textures: TextureMap, modelCollector: BiConsumer<Identifier, Supplier<JsonElement>>): Identifier {
        modelCollector.accept(id) { creator(textures) }
        return id
    }
}

fun TextureMap(vararg entries: Pair<TextureKey, Identifier>, initializer: TextureMap.() -> Unit = {}): TextureMap = TextureMap().apply {
    entries.forEach {
        put(it.first, it.second)
    }
    initializer()
}
