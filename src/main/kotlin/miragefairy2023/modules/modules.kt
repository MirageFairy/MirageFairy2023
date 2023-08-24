package miragefairy2023.modules

import miragefairy2023.module
import miragefairy2023.modules.entity.entityModule
import miragefairy2023.modules.fairy.fairyModule
import miragefairy2023.modules.fairyhouse.fairyHouseModule
import miragefairy2023.modules.passiveskill.passiveSkillModule
import miragefairy2023.modules.toolitem.toolItemModule

val modules = module {

    // システム系
    customDataModule()
    poemModule()
    lastFoodModule()
    itemTransportationModule()

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
        biomeModule()

        // アイテム追加系
        demonItemModule()
        mirageModule()
        luminariaModule()
        toolItemModule()

        // ブロック追加系
        demonBlockModule()
        fairyCrystalGlassModule()
        telescopeModule()
        fairyMailboxModule()
        fairyHouseModule()
        fairyModule()

        // クリエイティブ系
        debugModule()

    }

}
