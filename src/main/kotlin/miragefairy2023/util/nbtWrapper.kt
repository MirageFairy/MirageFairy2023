@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import mirrg.kotlin.hydrogen.castOrNull
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.nbt.NbtByte
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtDouble
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtFloat
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtLong
import net.minecraft.nbt.NbtShort
import net.minecraft.nbt.NbtString
import kotlin.reflect.KProperty


// EntryPoint

val NbtCompound.wrapper: NbtProvider<NbtCompound>
    get() {
        val nbt = this
        return object : NbtProvider<NbtCompound> {
            override fun getOrNull() = nbt
            override fun getOrCreate() = nbt
        }
    }


// NbtProvider

interface NbtProvider<out T> {
    fun getOrNull(): T?
    fun getOrCreate(): T
}

operator fun NbtProperty<NbtElement?, NbtElement>.get(key: String): NbtProperty<NbtElement?, NbtElement?> {
    val parent = this
    val nbtProvider = object : NbtProvider<NbtCompound> {
        override fun getOrNull() = parent.get() as? NbtCompound
        override fun getOrCreate() = getOrNull() ?: NbtCompound().also { parent.set(it) }
    }
    return nbtProvider[key]
}

operator fun NbtProperty<NbtElement?, NbtElement>.get(index: Int): NbtProperty<NbtElement?, NbtElement> {
    val parent = this
    val nbtProvider = object : NbtProvider<NbtList> {
        override fun getOrNull() = parent.get() as? NbtList
        override fun getOrCreate() = getOrNull() ?: NbtList().also { parent.set(it) }
    }
    return nbtProvider[index]
}


// NbtPath

operator fun NbtProvider<NbtCompound>.get(key: String): NbtProperty<NbtElement?, NbtElement?> {
    val parent = this
    return object : NbtProperty<NbtElement?, NbtElement?> {
        override fun get() = parent.getOrNull()?.get(key)
        override fun set(value: NbtElement?) {
            if (value != null) {
                parent.getOrCreate().put(key, value)
            } else {
                parent.getOrCreate().remove(key)
            }
        }
    }
}

operator fun NbtProvider<NbtList>.get(index: Int): NbtProperty<NbtElement?, NbtElement> {
    val parent = this
    return object : NbtProperty<NbtElement?, NbtElement> {
        override fun get() = parent.getOrNull()?.getOrNull(index)
        override fun set(value: NbtElement) {
            parent.getOrCreate()[index] = value
        }
    }
}


// NbtProperty

interface NbtProperty<out G, in S> {
    fun get(): G
    fun set(value: S)
}

inline fun <T> NbtProperty(crossinline getter: () -> T?, crossinline setter: (T) -> Unit) = object : NbtProperty<T?, T> {
    override fun get() = getter()
    override fun set(value: T) = setter(value)
}

val NbtProperty<NbtElement?, NbtElement>.byte get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.byteValue() }, { this.set(NbtByte.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.short get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.shortValue() }, { this.set(NbtShort.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.int get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.intValue() }, { this.set(NbtInt.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.long get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.longValue() }, { this.set(NbtLong.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.float get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.floatValue() }, { this.set(NbtFloat.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.double get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.doubleValue() }, { this.set(NbtDouble.of(it)) })
val NbtProperty<NbtElement?, NbtElement>.number get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.numberValue() }, { this.set(NbtDouble.of(it.toDouble())) })
val NbtProperty<NbtElement?, NbtElement>.string get() = NbtProperty({ this.get()?.asString() }, { this.set(NbtString.of(it)) })

val NbtProperty<NbtElement?, NbtElement>.map
    get() = NbtProperty({
        val nbt = this.get()?.castOrNull<NbtCompound>() ?: return@NbtProperty null
        nbt.keys.associate { key -> key!! to nbt[key]!! }
    }, { map ->
        this.set(NbtCompound().also { nbt ->
            map.forEach { entry ->
                nbt.put(entry.key, entry.value)
            }
        })
    })


// NbtDelegate

operator fun <T> NbtProperty<T, T>.getValue(thisRef: Any?, property: KProperty<*>): T = this.get()
operator fun <T> NbtProperty<T, T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) = this.set(value)

fun <T> NbtProperty<T?, T>.orDefault(getter: () -> T) = object : NbtProperty<T, T> {
    override fun get() = this@orDefault.get() ?: getter()
    override fun set(value: T) = this@orDefault.set(value)
}
