@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package miragefairy2023.util

import mirrg.kotlin.hydrogen.castOrNull
import mirrg.kotlin.hydrogen.unit
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

val NbtCompound.wrapper get() = RootNbtProvider(this)


// NbtProvider

interface NbtProvider<T> {
    fun getOrNull(): T?
    fun getOrCreate(): T
}

class RootNbtProvider(val nbt: NbtCompound) : NbtProvider<NbtCompound> {
    override fun getOrNull() = nbt
    override fun getOrCreate() = nbt
}

class CompoundNbtProvider(val nbtPath: NbtPath) : NbtProvider<NbtCompound> {
    override fun getOrNull() = nbtPath.get() as? NbtCompound
    override fun getOrCreate() = getOrNull() ?: NbtCompound().also { nbtPath.set(it) }
}

class ListNbtProvider(val nbtPath: NbtPath) : NbtProvider<NbtList> {
    override fun getOrNull() = nbtPath.get() as? NbtList
    override fun getOrCreate() = getOrNull() ?: NbtList().also { nbtPath.set(it) }
}

operator fun NbtPath.get(key: String) = CompoundNbtProvider(this)[key]
operator fun NbtPath.get(index: Int) = ListNbtProvider(this)[index]


// NbtPath

interface NbtPath {
    fun get(): NbtElement?
    fun set(nbt: NbtElement)
}

class CompoundElementNbtPath(val nbtProvider: NbtProvider<NbtCompound>, val key: String) : NbtPath {
    override fun get() = nbtProvider.getOrNull()?.get(key)
    override fun set(nbt: NbtElement) = unit { nbtProvider.getOrCreate().put(key, nbt) }
    fun removeTag() = nbtProvider.getOrCreate().remove(key)
}

class ListElementNbtPath(val nbtProvider: NbtProvider<NbtList>, val index: Int) : NbtPath {
    override fun get() = nbtProvider.getOrNull()?.getOrNull(index)
    override fun set(nbt: NbtElement) = unit { nbtProvider.getOrCreate()[index] = nbt }
}

operator fun NbtProvider<NbtCompound>.get(key: String) = CompoundElementNbtPath(this, key)
operator fun NbtProvider<NbtList>.get(index: Int) = ListElementNbtPath(this, index)


// NbtProperty

interface NbtProperty<G, S> {
    fun get(): G
    fun set(value: S)
}

inline fun <T> NbtProperty(crossinline getter: () -> T?, crossinline setter: (T) -> Unit) = object : NbtProperty<T?, T> {
    override fun get() = getter()
    override fun set(value: T) = setter(value)
}

val NbtPath.byte get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.byteValue() }, { this.set(NbtByte.of(it)) })
val NbtPath.short get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.shortValue() }, { this.set(NbtShort.of(it)) })
val NbtPath.int get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.intValue() }, { this.set(NbtInt.of(it)) })
val NbtPath.long get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.longValue() }, { this.set(NbtLong.of(it)) })
val NbtPath.float get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.floatValue() }, { this.set(NbtFloat.of(it)) })
val NbtPath.double get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.doubleValue() }, { this.set(NbtDouble.of(it)) })
val NbtPath.number get() = NbtProperty({ this.get()?.castOrNull<AbstractNbtNumber>()?.numberValue() }, { this.set(NbtDouble.of(it.toDouble())) })
val NbtPath.string get() = NbtProperty({ this.get()?.asString() }, { this.set(NbtString.of(it)) })

val NbtPath.map
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

interface NbtDelegate<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

fun <T> NbtProperty<T?, T>.orDefault(getter: () -> T) = object : NbtDelegate<T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>) = this@orDefault.get() ?: getter()
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = this@orDefault.set(value)
}
