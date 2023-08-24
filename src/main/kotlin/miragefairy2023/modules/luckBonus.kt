package miragefairy2023.modules

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.init.register
import miragefairy2023.util.jsonPrimitive
import miragefairy2023.util.randomInt
import mirrg.kotlin.hydrogen.atLeast
import mirrg.kotlin.hydrogen.atMost
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonSerializer
import net.minecraft.util.registry.Registry
import kotlin.math.pow

val luckBonusModule = module {
    register(Registry.LOOT_FUNCTION_TYPE, Identifier(MirageFairy2023.modId, "apply_luck_bonus"), applyLuckBonusLootFunctionType)
}

private val applyLuckBonusLootFunctionType = LootFunctionType(object : JsonSerializer<ApplyLuckBonusLootFunction> {
    override fun toJson(json: JsonObject, `object`: ApplyLuckBonusLootFunction, context: JsonSerializationContext) {
        if (`object`.factor != null) json.add("factor", `object`.factor.jsonPrimitive)
    }

    override fun fromJson(json: JsonObject, context: JsonDeserializationContext) = ApplyLuckBonusLootFunction(json["factor"]?.asDouble)
})

class ApplyLuckBonusLootFunction(val factor: Double? = null) : LootFunction {
    override fun getType() = applyLuckBonusLootFunctionType
    override fun apply(t: ItemStack, u: LootContext): ItemStack {
        val player = u[LootContextParameters.THIS_ENTITY] as? PlayerEntity ?: return t
        val luck = player.getAttributeValue(EntityAttributes.GENERIC_LUCK)

        val itemStack = t.copy()
        val factor = when {
            luck > 0 -> 1.0 + (factor ?: 0.2) * luck // 正効果
            luck < 0 -> (1.0 - (factor ?: 0.2)).pow(-luck) // 負効果
            else -> 1.0
        }
        itemStack.count = u.random.randomInt(t.count * factor) atLeast 0 atMost itemStack.maxCount

        return itemStack
    }
}
