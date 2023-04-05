package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.util.createItemStack
import miragefairy2023.util.enJaItemGroup
import miragefairy2023.util.itemEntry
import miragefairy2023.util.lootPool
import miragefairy2023.util.lootTable
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

val commonItemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(MirageFairy2023.modId, "common")) { DemonItemCard.XARPITE().createItemStack() }
val tier1LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier1_fairy_crystal")
val tier2LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier2_fairy_crystal")
val tier3LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier3_fairy_crystal")

val commonModule = module {

    // メインアイテムグループ
    enJaItemGroup({ commonItemGroup }, "MirageFairy2023", "MirageFairy2023")

    // 実績報酬ルートテーブル
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier1LootTableId, lootTable {
            pool(lootPool {
                with(itemEntry(DemonItemCard.HONORABLE_FAIRY_CRYSTAL()))
            })
        })
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier2LootTableId, lootTable {
            pool(lootPool {
                with(itemEntry(DemonItemCard.GLORIOUS_FAIRY_CRYSTAL()))
            })
        })
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier3LootTableId, lootTable {
            pool(lootPool {
                with(itemEntry(DemonItemCard.LEGENDARY_FAIRY_CRYSTAL()))
            })
        })
    }

}
