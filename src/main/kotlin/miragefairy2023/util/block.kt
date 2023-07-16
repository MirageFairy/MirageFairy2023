package miragefairy2023.util

import miragefairy2023.modules.DemonParticleTypeCard
import net.minecraft.block.Block
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

/** 初期化コンテキストで呼び出すことはできません。 */
val Block.identifier get(): Identifier = Registry.BLOCK.getId(this)

fun Block.addAvailableParticle(world: World, blockPos: BlockPos) {
    val random = world.random
    if (random.nextInt(1) == 0) {
        val x = blockPos.x.toDouble() + 0.0 + random.nextDouble() * 1.0
        val y = blockPos.y.toDouble() + 0.0 + random.nextDouble() * 0.5
        val z = blockPos.z.toDouble() + 0.0 + random.nextDouble() * 1.0
        world.addParticle(
            DemonParticleTypeCard.MISSION.particleType,
            x, y, z,
            random.nextGaussian() * 0.00,
            random.nextGaussian() * 0.00 + 0.4,
            random.nextGaussian() * 0.00,
        )
    }
}
