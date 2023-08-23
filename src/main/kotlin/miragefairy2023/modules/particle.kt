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
    COLLECTING_MAGIC("collecting_magic", "magic", DemonParticleBehaviour.ENCHANT, false),
    DESCENDING_MAGIC("descending_magic", "magic", DemonParticleBehaviour.END_ROD, false),
    MIRAGE_FLOUR("mirage_flour", "mirage_flour", DemonParticleBehaviour.HAPPY, false),
    ATTRACTING_MAGIC("attracting_magic", "mission", DemonParticleBehaviour.ATTRACTING, false),
    AURA("aura", "mission", DemonParticleBehaviour.END_ROD, false),
    ;

    val particleType: DefaultParticleType = FabricParticleTypes.simple(alwaysSpawn)
}

val particleModule = module {
    DemonParticleTypeCard.values().forEach { card ->

        // データファイル生成
        onGenerateParticles {
            it[Identifier(MirageFairy2023.modId, card.path)] = jsonObjectOf(
                "textures" to jsonArrayOf(
                    "miragefairy2023:${card.textureName}".jsonPrimitive,
                ),
            )
        }

        // 登録
        Registry.register(Registry.PARTICLE_TYPE, Identifier(MirageFairy2023.modId, card.path), card.particleType)

        // クライアント側でファクトリを登録
        onInitializeClient {
            MirageFairy2023.clientProxy!!.registerParticleFactory(card.particleType, card.demonParticleBehaviour)
        }

    }
}
