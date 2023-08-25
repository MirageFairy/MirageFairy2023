package miragefairy2023.util.init

import miragefairy2023.InitializationScope
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.item.Item
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

fun InitializationScope.generateItemTag(tag: TagKey<Item>, item: Item) = onGenerateItemTags { it(tag).add(item) }
fun InitializationScope.generateBlockTag(tag: TagKey<Block>, block: Block) = onGenerateBlockTags { it(tag).add(block) }
fun InitializationScope.generateEntityTypeTag(tag: TagKey<EntityType<*>>, entityType: EntityType<*>) = onGenerateEntityTypeTags { it(tag).add(entityType) }
fun InitializationScope.generateBiomeTag(tag: TagKey<Biome>, identifier: Identifier) = onGenerateBiomeTags { it(tag).add(identifier) }
