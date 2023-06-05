package miragefairy2023.api

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier

interface PassiveSkillItem {
    val passiveSkillProvider: PassiveSkillProvider
}

interface PassiveSkillProvider {

    /**
     * 同時発動を抑制するための識別子です。
     */
    val identifier: Identifier

    /**
     * このアイテムのベースの強さを表します。
     * スケールは妖精のレア度と同じですが、小数値を持ち、様々な補正が加算されます。
     * この値はパッシブスキルに乗じられるため、0を超えることが推奨されます。
     */
    val mana: Double

    fun getPassiveSkills(player: PlayerEntity, itemStack: ItemStack): List<PassiveSkill>

}

class PassiveSkill(val conditions: List<PassiveSkillCondition>, val effect: PassiveSkillEffect)

interface PassiveSkillCondition {
    fun getText(): Text
    fun test(player: PlayerEntity, mana: Double): Boolean
}

interface PassiveSkillEffect {

    /**
     * @param baseEfficiency [efficiency]と同様ですが、パッシブスキルの補正が考慮されません。
     * @param efficiency 妖精のレア度10相当のときに1.0になります。
     */
    fun getText(baseEfficiency: Double, efficiency: Double): Text

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

    /**
     * @param baseEfficiency パッシブスキルの補正が考慮されません。
     */
    fun getMana(baseEfficiency: Double) = 0.0

}
