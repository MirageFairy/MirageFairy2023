package miragefairy2023.modules

import miragefairy2023.DemonParticleBehaviour
import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.jsonArrayOf
import miragefairy2023.util.jsonObjectOf
import miragefairy2023.util.jsonPrimitive
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonParticleTypeCard(
    val path: String,
    val textureName: String,
    val demonParticleBehaviour: DemonParticleBehaviour,
    alwaysSpawn: Boolean
) {
    MISSION("mission", "mission", DemonParticleBehaviour.HAPPY, true),
    ;

    val particleType: DefaultParticleType = FabricParticleTypes.simple(alwaysSpawn)
}

object ParticleModule {
    val init = module {
        DemonParticleTypeCard.values().forEach { card ->

            // データファイル生成
            onGenerateParticles {
                it[Identifier(modId, card.path)] = jsonObjectOf(
                    "textures" to jsonArrayOf(
                        "miragefairy2023:${card.textureName}".jsonPrimitive,
                    ),
                )
            }

            // 登録
            Registry.register(Registry.PARTICLE_TYPE, Identifier(modId, card.path), card.particleType)

            // クライアント側でファクトリを登録
            onInitializeClient {
                MirageFairy2023.clientProxy!!.registerParticleFactory(card.particleType, card.demonParticleBehaviour)
            }

        }
    }
}
