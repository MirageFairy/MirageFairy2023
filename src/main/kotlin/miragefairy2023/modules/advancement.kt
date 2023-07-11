package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.modules.fairy.fairiesItemTag
import miragefairy2023.modules.fairy.fairiesOfRareTags
import miragefairy2023.modules.fairy.invoke
import miragefairy2023.modules.fairyhouse.fairyMetamorphosisAltar
import miragefairy2023.modules.toolitem.ToolItemCard
import miragefairy2023.util.init.advancement
import miragefairy2023.util.init.criterion
import miragefairy2023.util.init.itemLootPoolEntry
import miragefairy2023.util.init.lootPool
import miragefairy2023.util.init.lootTable
import miragefairy2023.util.init.reward
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.CriterionMerger
import net.minecraft.util.Identifier

val tier1LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier1_fairy_crystal")
val tier2LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier2_fairy_crystal")
val tier3LootTableId = Identifier(MirageFairy2023.modId, "advancement_reward/tier3_fairy_crystal")

val advancementModule = module {

    // 進捗報酬ルートテーブル
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier1LootTableId, lootTable {
            pool(lootPool {
                with(itemLootPoolEntry(DemonItemCard.HONORABLE_FAIRY_CRYSTAL.item.feature))
            })
        })
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier2LootTableId, lootTable {
            pool(lootPool {
                with(itemLootPoolEntry(DemonItemCard.GLORIOUS_FAIRY_CRYSTAL.item.feature))
            })
        })
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier3LootTableId, lootTable {
            pool(lootPool {
                with(itemLootPoolEntry(DemonItemCard.LEGENDARY_FAIRY_CRYSTAL.item.feature))
            })
        })
    }

    // 進捗
    @Suppress("UNUSED_VARIABLE")
    run {

        val root = advancement(
            "root", { mirageSeedItem.feature },
            "MirageFairy2023: Mainstream", "MirageFairy2023: メインストリーム",
            "Harvest a Mirage flower", "ミラージュの花に触れてみる",
            backgroundTexture = Identifier(modId, "textures/block/fairy_wood_log.png"),
        ) {
            criteriaMerger(CriterionMerger.OR)
            criterion(MirageFlourCard.TINY_MIRAGE_FLOUR.item.feature)
            criterion(mirageSeedItem.feature)
            reward(tier1LootTableId)
        }

        val mirageFlour = advancement(
            "mirage_flour", { MirageFlourCard.MIRAGE_FLOUR.item.feature },
            "Pile of Pollen", "ある程度の神秘",
            "Compact the pollen", "ミラージュの花粉をひとまとまりにする",
            parent = root,
        ) {
            criterion(MirageFlourCard.MIRAGE_FLOUR.item.feature)
            reward(tier1LootTableId)
        }

        val rareMirageFlour = advancement(
            "rare_mirage_flour", { MirageFlourCard.RARE_MIRAGE_FLOUR.item.feature },
            "Fluorescent Structural Color", "ほたる色の誘惑",
            "Select high-quality pollen carefully", "良質な花粉を厳選する",
            parent = mirageFlour,
        ) {
            criterion(MirageFlourCard.RARE_MIRAGE_FLOUR.item.feature)
            reward(tier1LootTableId)
        }

        val veryRareMirageFlour = advancement(
            "very_rare_mirage_flour", { MirageFlourCard.VERY_RARE_MIRAGE_FLOUR.item.feature },
            "A Fairy Who Thinks He Is Kind of Human", "失われた科学",
            "Remove impurities with static electricity", "更にふわふわな花粉を選別する",
            parent = rareMirageFlour,
        ) {
            criterion(MirageFlourCard.VERY_RARE_MIRAGE_FLOUR.item.feature)
            reward(tier1LootTableId)
        }

        val ultraRareMirageFlour = advancement(
            "ultra_rare_mirage_flour", { MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR.item.feature },
            "Fairylands", "世界の其処彼処に在ると云われる御伽の国",
            "Precipitate aura crystals", "花粉に含まれるオーラ分を濃縮する",
            parent = veryRareMirageFlour,
        ) {
            criterion(MirageFlourCard.ULTRA_RARE_MIRAGE_FLOUR.item.feature)
            reward(tier2LootTableId)
        }

        val superRareMirageFlour = advancement(
            "super_rare_mirage_flour", { MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR.item.feature },
            "Retrograde Wavelength", "天空に昇る神秘の波動",
            "Condense until astral radiation appears", "ふるいにかけることで宇宙エネルギーを刻み込む",
            parent = ultraRareMirageFlour,
        ) {
            criterion(MirageFlourCard.SUPER_RARE_MIRAGE_FLOUR.item.feature)
            reward(tier2LootTableId)
        }

        val extremelyRareMirageFlour = advancement(
            "extremely_rare_mirage_flour", { MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR.item.feature },
            "Miravitational Lens", "ゆがむ空間",
            "Excite ether field to 80% of local vacuum collapse", "神秘のパワーをミラージュが見えるまで濃縮する",
            parent = superRareMirageFlour,
            frame = AdvancementFrame.CHALLENGE,
        ) {
            criterion(MirageFlourCard.EXTREMELY_RARE_MIRAGE_FLOUR.item.feature)
            reward(tier3LootTableId)
        }

        val fairy = advancement(
            "fairy", { FairyCard.AIR() },
            "Pollen Mimicking the Fairy", "自我を持つ植物",
            "Get a fairy with Mirage flour", "ミラージュの花粉を使用し、妖精を得る",
            parent = root,
        ) {
            criterion(fairiesItemTag)
            reward(tier1LootTableId)
        }

        val rare1Fairy = advancement(
            "rare1_fairy", { FairyCard.DIRT() }, // TODO 変更
            "The Phantasmal Law", "幻想を司るもの",
            "Observe astral vortices from 'inside the mind'", "妖精を遣い、人間の心の中を探検する",
            parent = fairy,
        ) {
            criterion(fairiesOfRareTags(1))
            reward(tier1LootTableId)
        }

        val rare2Fairy = advancement(
            "rare2_fairy", { FairyCard.PLAINS() }, // TODO 変更
            "Dreamy Flower", "植物の見る夢",
            "Say hello when someone says hello", "妖精は何を感じ、何を思うのか？",
            parent = rare1Fairy,
        ) {
            criterion(fairiesOfRareTags(2))
            reward(tier1LootTableId)
        }

        val rare3Fairy = advancement(
            "rare3_fairy", { FairyCard.FOREST() }, // TODO 変更
            "Real Elephants and Imaginary Elephants", "人間が想像できることは人間が実現する",
            "Brainstorm the Mirage miracle", "世の中のくだらない奇跡について無限に思いを巡らせてみる",
            parent = rare2Fairy,
        ) {
            criterion(fairiesOfRareTags(3))
            reward(tier1LootTableId)
        }

        val rare4Fairy = advancement(
            "rare4_fairy", { FairyCard.IRON() },
            "Fairy Diary", "八百万の妖精",
            "Meditate on familiar things", "身の回りのすこしばかりよいものを見つめてみる",
            parent = rare3Fairy,
        ) {
            criterion(fairiesOfRareTags(4))
            reward(tier1LootTableId)
        }

        val rare5Fairy = advancement(
            "rare5_fairy", { FairyCard.PLAYER() },
            "The Infinite Potential Tower", "バベルの水道橋",
            "Try to obtain particles in the world", "「ヒト」の妖精を見、「ヒト」の限界を知る",
            parent = rare4Fairy,
        ) {
            criterion(fairiesOfRareTags(5))
            reward(tier1LootTableId)
        }

        val rare6Fairy = advancement(
            "rare6_fairy", { FairyCard.SPRUCE() }, // TODO 変更
            "Nature Nation", "花鳥風月",
            "Run out of the home and see what you can see", "身近な自然に心を躍らせる",
            parent = rare5Fairy,
        ) {
            criterion(fairiesOfRareTags(6))
            reward(tier1LootTableId)
        }

        val rare7Fairy = advancement(
            "rare7_fairy", { FairyCard.WARDEN() },
            "Subterranean Silence", "不気味の谷を往く",
            "Step into abyss", "深淵を覗く",
            parent = rare6Fairy,
        ) {
            criterion(fairiesOfRareTags(7))
            reward(tier2LootTableId)
        }

        val rare8Fairy = advancement(
            "rare8_fairy", { FairyCard.AVALON() },
            "Garden of Avalon", "須弥山の頂",
            "Step into eden", "楽園の夢を見る",
            parent = rare7Fairy,
        ) {
            criterion(fairiesOfRareTags(8))
            reward(tier2LootTableId)
        }

        val rare9Fairy = advancement(
            "rare9_fairy", { FairyCard.MOON() },
            "Lunatic Time", "朧月の夜",
            "Blown by cold wind and exposed to cosmic rays", "縁側でお団子を食べよう",
            parent = rare8Fairy,
        ) {
            criterion(fairiesOfRareTags(9))
            reward(tier2LootTableId)
        }

        val rare10Fairy = advancement(
            "rare10_fairy", { FairyCard.SUN() },
            "More Precious Than the Sun", "サンフラワーロック",
            "Reflector attracts sky fairies", "天上の妖精の手を引き、地上を覗かせる",
            parent = rare9Fairy,
        ) {
            criterion(fairiesOfRareTags(10))
            reward(tier2LootTableId)
        }

        val rare11Fairy = advancement(
            "rare11_fairy", { FairyCard.VOID() }, // TODO 変更
            "What Is It Like to Be a Fairy?", "妖精であるとはどのようなことか",
            "Feel the cosmic astral radiation", "天を超えた世界の事象を知る",
            parent = rare10Fairy,
        ) {
            criterion(fairiesOfRareTags(11))
            reward(tier2LootTableId)
        }

        val rare12Fairy = advancement(
            "rare12_fairy", { FairyCard.TIME() },
            "The Physical Law", "空間を司るもの",
            "Observe astral vortices from 'outside the universe'", "妖精を遣い、神の心の中を探検する",
            parent = rare11Fairy,
            frame = AdvancementFrame.CHALLENGE,
        ) {
            criterion(fairiesOfRareTags(12))
            reward(tier3LootTableId)
        }

        val dreamCatcher = advancement(
            "dream_catcher", { ToolItemCard.DREAM_CATCHER.item.feature },
            "Dreamy Filtration Medium", "夢取り網",
            "Make a tool to collect good dreams", "夢を集める道具を作る",
            parent = root,
        ) {
            criterion(ToolItemCard.DREAM_CATCHER.item.feature)
            reward(tier1LootTableId)
        }

        val blueDreamCatcher = advancement(
            "blue_dream_catcher", { ToolItemCard.BLUE_DREAM_CATCHER.item.feature },
            "Law of the Happiness", "しあわせは儚き渦の中心に",
            "Adsorb viral memes with reticulated anti-entropy", "秩序の宝石で邪気を分解する",
            parent = dreamCatcher,
        ) {
            criterion(ToolItemCard.BLUE_DREAM_CATCHER.item.feature)
            reward(tier2LootTableId)
        }

        val artificialFairyCrystal = advancement(
            "artificial_fairy_crystal", { DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item.feature },
            "Organic Amorphous Material", "水晶の飴",
            "Coagulate sugar contained in pollen", "花粉に含まれる糖分を凝固させる",
            parent = root,
        ) {
            criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item.feature)
            reward(tier1LootTableId)
        }

        val telescope = advancement(
            "telescope", { telescopeBlockItem.feature },
            "Living in the Universe", "ユニバースに佇む",
            "Investigate the world unknown to fairies", "レンズを通して妖精の知らない世界を見る",
            parent = artificialFairyCrystal,
        ) {
            criterion(telescopeBlockItem.feature)
            reward(tier1LootTableId)
        }

        val fairyMetamorphosisAltar = advancement(
            "fairy_metamorphosis_altar", { fairyMetamorphosisAltar.blockItem.feature },
            "The Unknown World of Thaumaturgy", "魔術の世界",
            "Cause a arcane reaction", "神秘反応を起こす",
            parent = root,
        ) {
            criterion(fairyMetamorphosisAltar.blockItem.feature)
            reward(tier1LootTableId)
        }

        val chaosStone = advancement(
            "chaos_stone", { DemonItemCard.CHAOS_STONE.item.feature },
            "The World of Science", "知られざる科学の世界",
            "Cause a chemical reaction", "化学反応を起こす",
            parent = fairyMetamorphosisAltar,
        ) {
            criterion(DemonItemCard.CHAOS_STONE.item.feature)
            reward(tier2LootTableId)
        }

    }

}
