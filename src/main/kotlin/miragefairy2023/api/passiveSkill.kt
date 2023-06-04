package miragefairy2023.api

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

interface PassiveSkillItem {
    fun getPassiveSkillIdentifier(): Identifier
    fun getPassiveSkillLevel(): Double
    fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack): List<PassiveSkill>
}

class PassiveSkill(val conditions: List<PassiveSkillCondition>, val effect: PassiveSkillEffect)

interface PassiveSkillCondition {
    fun getText(): Text
    fun test(player: PlayerEntity, itemStack: ItemStack): Boolean
}

interface PassiveSkillEffect {

    /**
     * @param efficiency 妖精のレア度10相当のときに1.0になります。
     */
    fun getText(efficiency: Double): Text

    /**
     * 任意のタイミングで更新されうる持続的な効果を更新します。
     * プレイヤーやサーバーのリロードに伴って揮発するか、制限時間付きの効果を使用する必要があります。
     * このメソッドは必ず論理サーバーで呼び出されます。
     * @param efficiency 妖精のレア度10相当のときに1.0になります。
     * @param terminators プレイヤーのアンロードおよびサーバーの終了時は呼び出されません。
     */
    fun update(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>, terminators: MutableList<() -> Unit>) = Unit

    /**
     * 10秒おきに呼び出されるアクションを発揮します。
     * このメソッドは必ず論理サーバーで呼び出されます。
     * @param efficiency 妖精のレア度10相当のときに1.0になります。
     */
    fun affect(world: ServerWorld, player: PlayerEntity, efficiency: Double, passiveSkillVariable: MutableMap<Identifier, Any>, initializers: MutableList<() -> Unit>) = Unit

}
