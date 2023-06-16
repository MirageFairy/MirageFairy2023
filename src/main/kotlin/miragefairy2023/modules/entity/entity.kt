package miragefairy2023.modules.entity

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier

val entityModule = module {
    antimatterBoltModule()
}

class DemonEntityTypeCard<E : Entity>(val path: String, spawnGroup: SpawnGroup, width: Float, height: Float, entityFactory: EntityType.EntityFactory<E>) {
    val identifier = Identifier(MirageFairy2023.modId, path)
    val entityType: EntityType<E> = FabricEntityTypeBuilder.create(spawnGroup, entityFactory)
        .dimensions(EntityDimensions.fixed(width, height))
        .build()
}
