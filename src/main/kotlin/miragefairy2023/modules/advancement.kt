package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.module
import miragefairy2023.modules.fairy.FairyCard
import miragefairy2023.modules.fairy.fairiesItemTag
import miragefairy2023.modules.fairy.fairiesOfRareTags
import miragefairy2023.modules.fairyhouse.fairyMetamorphosisAltar
import miragefairy2023.modules.toolitem.ToolItemCard
import miragefairy2023.util.datagen.ItemLootPoolEntry
import miragefairy2023.util.datagen.LootPool
import miragefairy2023.util.datagen.LootTable
import miragefairy2023.util.init.advancement
import miragefairy2023.util.init.criterion
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
        consumer.accept(tier1LootTableId, LootTable(LootPool(ItemLootPoolEntry(DemonItemCard.HONORABLE_FAIRY_CRYSTAL.item))))
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier2LootTableId, LootTable(LootPool(ItemLootPoolEntry(DemonItemCard.GLORIOUS_FAIRY_CRYSTAL.item))))
    }
    onGenerateAdvancementRewardLootTables { consumer ->
        consumer.accept(tier3LootTableId, LootTable(LootPool(ItemLootPoolEntry(DemonItemCard.LEGENDARY_FAIRY_CRYSTAL.item))))
    }

    // 進捗
    @Suppress("UNUSED_VARIABLE")
    run {

        val root = advancement(
            "root", Mirage.seedItem,
            "MirageFairy2023: Mainstream", "MirageFairy2023: メインストリーム",
            "Harvest a Mirage flower", "ミラージュの花に触れてみる",
            backgroundTexture = Identifier(MirageFairy2023.modId, "textures/block/fairy_wood_log.png"),
        ) {
            criteriaMerger(CriterionMerger.OR)
            criterion(DemonItemCard.TINY_MIRAGE_FLOUR.item)
            criterion(Mirage.seedItem)
            reward(tier1LootTableId)
        }

        val mirageFlour = advancement(
            "mirage_flour", DemonItemCard.MIRAGE_FLOUR.item,
            "Pile of Pollen", "ある程度の神秘",
            "Compact the pollen", "ミラージュの花粉をひとまとまりにする",
            parent = root,
        ) {
            criterion(DemonItemCard.MIRAGE_FLOUR.item)
            reward(tier1LootTableId)
        }

        val rareMirageFlour = advancement(
            "rare_mirage_flour", DemonItemCard.RARE_MIRAGE_FLOUR.item,
            "Fluorescent Structural Color", "ほたる色の誘惑",
            "Select high-quality pollen carefully", "良質な花粉を厳選する",
            parent = mirageFlour,
        ) {
            criterion(DemonItemCard.RARE_MIRAGE_FLOUR.item)
            reward(tier1LootTableId)
        }

        val veryRareMirageFlour = advancement(
            "very_rare_mirage_flour", DemonItemCard.VERY_RARE_MIRAGE_FLOUR.item,
            "A Fairy Who Thinks He Is Kind of Human", "失われた科学",
            "Remove impurities with static electricity", "更にふわふわな花粉を選別する",
            parent = rareMirageFlour,
        ) {
            criterion(DemonItemCard.VERY_RARE_MIRAGE_FLOUR.item)
            reward(tier1LootTableId)
        }

        val ultraRareMirageFlour = advancement(
            "ultra_rare_mirage_flour", DemonItemCard.ULTRA_RARE_MIRAGE_FLOUR.item,
            "Fairylands", "世界の其処彼処に在ると云われる御伽の国",
            "Precipitate aura crystals", "花粉に含まれるオーラ分を濃縮する",
            parent = veryRareMirageFlour,
        ) {
            criterion(DemonItemCard.ULTRA_RARE_MIRAGE_FLOUR.item)
            reward(tier2LootTableId)
        }

        val superRareMirageFlour = advancement(
            "super_rare_mirage_flour", DemonItemCard.SUPER_RARE_MIRAGE_FLOUR.item,
            "Retrograde Wavelength", "天空に昇る神秘の波動",
            "Condense until astral radiation appears", "ふるいにかけることで宇宙エネルギーを刻み込む",
            parent = ultraRareMirageFlour,
        ) {
            criterion(DemonItemCard.SUPER_RARE_MIRAGE_FLOUR.item)
            reward(tier2LootTableId)
        }

        val extremelyRareMirageFlour = advancement(
            "extremely_rare_mirage_flour", DemonItemCard.EXTREMELY_RARE_MIRAGE_FLOUR.item,
            "Miravitational Lens", "ゆがむ空間",
            "Excite ether field to 80% of local vacuum collapse", "神秘のパワーをミラージュが見えるまで濃縮する",
            parent = superRareMirageFlour,
            frame = AdvancementFrame.CHALLENGE,
        ) {
            criterion(DemonItemCard.EXTREMELY_RARE_MIRAGE_FLOUR.item)
            reward(tier3LootTableId)
        }

        val fairy = advancement(
            "fairy", FairyCard.AIR[1].item,
            "Pollen Mimicking the Fairy", "自我を持つ植物",
            "Get a fairy with Mirage flour", "ミラージュの花粉を使用し、妖精を得る",
            parent = root,
        ) {
            criterion(fairiesItemTag)
            reward(tier1LootTableId)
        }

        val rare1Fairy = advancement(
            "rare1_fairy", FairyCard.DIRT[1].item, // TODO 変更
            "The Phantasmal Law", "幻想を司るもの",
            "Observe astral vortices from 'inside the mind'", "妖精を遣い、人間の心の中を探検する",
            parent = fairy,
        ) {
            criterion(fairiesOfRareTags(1))
            reward(tier1LootTableId)
        }

        val rare2Fairy = advancement(
            "rare2_fairy", FairyCard.PLAINS[1].item, // TODO 変更
            "Dreamy Flower", "植物の見る夢",
            "Say hello when someone says hello", "妖精は何を感じ、何を思うのか？",
            parent = rare1Fairy,
        ) {
            criterion(fairiesOfRareTags(2))
            reward(tier1LootTableId)
        }

        val rare3Fairy = advancement(
            "rare3_fairy", FairyCard.FOREST[1].item, // TODO 変更
            "Real Elephants and Imaginary Elephants", "人間が想像できることは人間が実現する",
            "Brainstorm the Mirage miracle", "世の中のくだらない奇跡について無限に思いを巡らせてみる",
            parent = rare2Fairy,
        ) {
            criterion(fairiesOfRareTags(3))
            reward(tier1LootTableId)
        }

        val rare4Fairy = advancement(
            "rare4_fairy", FairyCard.IRON[1].item,
            "Fairy Diary", "八百万の妖精",
            "Meditate on familiar things", "身の回りのすこしばかりよいものを見つめてみる",
            parent = rare3Fairy,
        ) {
            criterion(fairiesOfRareTags(4))
            reward(tier1LootTableId)
        }

        val rare5Fairy = advancement(
            "rare5_fairy", FairyCard.PLAYER[1].item,
            "The Infinite Potential Tower", "バベルの水道橋",
            "Try to obtain particles in the world", "「ヒト」の妖精を見、「ヒト」の限界を知る",
            parent = rare4Fairy,
        ) {
            criterion(fairiesOfRareTags(5))
            reward(tier1LootTableId)
        }

        val rare6Fairy = advancement(
            "rare6_fairy", FairyCard.SPRUCE[1].item, // TODO 変更
            "Nature Nation", "花鳥風月",
            "Run out of the home and see what you can see", "身近な自然に心を躍らせる",
            parent = rare5Fairy,
        ) {
            criterion(fairiesOfRareTags(6))
            reward(tier1LootTableId)
        }

        val rare7Fairy = advancement(
            "rare7_fairy", FairyCard.WARDEN[1].item,
            "Subterranean Silence", "不気味の谷を往く",
            "Step into abyss", "深淵を覗く",
            parent = rare6Fairy,
        ) {
            criterion(fairiesOfRareTags(7))
            reward(tier2LootTableId)
        }

        val rare8Fairy = advancement(
            "rare8_fairy", FairyCard.AVALON[1].item,
            "Garden of Avalon", "須弥山の頂",
            "Step into eden", "楽園の夢を見る",
            parent = rare7Fairy,
        ) {
            criterion(fairiesOfRareTags(8))
            reward(tier2LootTableId)
        }

        val rare9Fairy = advancement(
            "rare9_fairy", FairyCard.MOON[1].item,
            "Lunatic Time", "朧月の夜",
            "Blown by cold wind and exposed to cosmic rays", "縁側でお団子を食べよう",
            parent = rare8Fairy,
        ) {
            criterion(fairiesOfRareTags(9))
            reward(tier2LootTableId)
        }

        val rare10Fairy = advancement(
            "rare10_fairy", FairyCard.SUN[1].item,
            "More Precious Than the Sun", "サンフラワーロック",
            "Reflector attracts sky fairies", "天上の妖精の手を引き、地上を覗かせる",
            parent = rare9Fairy,
        ) {
            criterion(fairiesOfRareTags(10))
            reward(tier2LootTableId)
        }

        val rare11Fairy = advancement(
            "rare11_fairy", FairyCard.VOID[1].item, // TODO 変更
            "What Is It Like to Be a Fairy?", "妖精であるとはどのようなことか",
            "Feel the cosmic astral radiation", "天を超えた世界の事象を知る",
            parent = rare10Fairy,
        ) {
            criterion(fairiesOfRareTags(11))
            reward(tier2LootTableId)
        }

        val rare12Fairy = advancement(
            "rare12_fairy", FairyCard.TIME[1].item,
            "The Physical Law", "空間を司るもの",
            "Observe astral vortices from 'outside the universe'", "妖精を遣い、神の心の中を探検する",
            parent = rare11Fairy,
            frame = AdvancementFrame.CHALLENGE,
        ) {
            criterion(fairiesOfRareTags(12))
            reward(tier3LootTableId)
        }

        val dreamCatcher = advancement(
            "dream_catcher", ToolItemCard.DREAM_CATCHER.item,
            "Dreamy Filtration Medium", "夢取り網",
            "Make a tool to collect good dreams", "夢を集める道具を作る",
            parent = root,
        ) {
            criterion(ToolItemCard.DREAM_CATCHER.item)
            reward(tier1LootTableId)
        }

        val blueDreamCatcher = advancement(
            "blue_dream_catcher", ToolItemCard.BLUE_DREAM_CATCHER.item,
            "Law of the Happiness", "しあわせは儚き渦の中心に",
            "Adsorb viral memes with reticulated anti-entropy", "秩序の宝石で邪気を分解する",
            parent = dreamCatcher,
        ) {
            criterion(ToolItemCard.BLUE_DREAM_CATCHER.item)
            reward(tier2LootTableId)
        }

        val artificialFairyCrystal = advancement(
            "artificial_fairy_crystal", DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item,
            "Organic Amorphous Material", "水晶の飴",
            "Coagulate sugar contained in pollen", "花粉に含まれる糖分を凝固させる",
            parent = root,
        ) {
            criterion(DemonItemCard.ARTIFICIAL_FAIRY_CRYSTAL.item)
            reward(tier1LootTableId)
        }

        val telescope = advancement(
            "telescope", Telescope.item,
            "Living in the Universe", "ユニバースに佇む",
            "Investigate the world unknown to fairies", "レンズを通して妖精の知らない世界を見る",
            parent = artificialFairyCrystal,
        ) {
            criterion(Telescope.item)
            reward(tier1LootTableId)
        }

        val fairyMetamorphosisAltar = advancement(
            "fairy_metamorphosis_altar", fairyMetamorphosisAltar.item,
            "The Unknown World of Thaumaturgy", "魔術の世界",
            "Cause a arcane reaction", "神秘反応を起こす",
            parent = root,
        ) {
            criterion(fairyMetamorphosisAltar.item)
            reward(tier1LootTableId)
        }

        val chaosStone = advancement(
            "chaos_stone", DemonItemCard.CHAOS_STONE.item,
            "The World of Science", "知られざる科学の世界",
            "Cause a chemical reaction", "化学反応を起こす",
            parent = fairyMetamorphosisAltar,
        ) {
            criterion(DemonItemCard.CHAOS_STONE.item)
            reward(tier2LootTableId)
        }

    }

}
