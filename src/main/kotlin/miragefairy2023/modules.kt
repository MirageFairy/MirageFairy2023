package miragefairy2023

import miragefairy2023.modules.advancementModule
import miragefairy2023.modules.commonModule
import miragefairy2023.modules.customDataModule
import miragefairy2023.modules.debugModule
import miragefairy2023.modules.demonBlockModule
import miragefairy2023.modules.demonItemModule
import miragefairy2023.modules.enchantmentModule
import miragefairy2023.modules.entity.entityModule
import miragefairy2023.modules.fairy.fairyModule
import miragefairy2023.modules.fairyCrystalGlassModule
import miragefairy2023.modules.fairyhouse.fairyHouseModule
import miragefairy2023.modules.lastFoodModule
import miragefairy2023.modules.luckBonusModule
import miragefairy2023.modules.mirageFlourModule
import miragefairy2023.modules.mirageFlowerModule
import miragefairy2023.modules.particleModule
import miragefairy2023.modules.passiveskill.passiveSkillModule
import miragefairy2023.modules.playerAttributeModule
import miragefairy2023.modules.poemModule
import miragefairy2023.modules.reiModule
import miragefairy2023.modules.soundEventModule
import miragefairy2023.modules.telescopeModule
import miragefairy2023.modules.toolMaterialModule
import miragefairy2023.modules.toolitem.toolItemModule
import miragefairy2023.modules.trinketsSlotModule

val modules = module {

    // システム系
    customDataModule()
    poemModule()
    lastFoodModule()

    // コンポーネント系
    passiveSkillModule()
    particleModule()
    luckBonusModule()
    toolMaterialModule()
    playerAttributeModule()
    soundEventModule()
    reiModule()
    enchantmentModule()

    // コンテンツ系
    run {

        // 共用コンテンツの生成
        commonModule()
        trinketsSlotModule()
        advancementModule()
        entityModule()

        // アイテム追加系
        demonItemModule()
        mirageFlowerModule()
        mirageFlourModule()
        toolItemModule()

        // ブロック追加系
        demonBlockModule()
        fairyCrystalGlassModule()
        telescopeModule()
        fairyHouseModule()
        fairyModule()

        // クリエイティブ系
        debugModule()

    }

}
