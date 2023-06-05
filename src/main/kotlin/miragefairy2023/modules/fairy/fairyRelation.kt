package miragefairy2023.modules.fairy

import miragefairy2023.api.Fairy
import net.minecraft.block.Block
import net.minecraft.entity.EntityType

class BlockFairyRelation(val block: Block, val fairy: Fairy)
class EntityTypeFairyRelation(val entityType: EntityType<*>, val fairy: Fairy)

val BLOCK_FAIRY_RELATION_LIST = mutableListOf<BlockFairyRelation>()
val ENTITY_TYPE_FAIRY_RELATION_LIST = mutableListOf<EntityTypeFairyRelation>()
