package miragefairy2023

import miragefairy2023.modules.commonModule
import miragefairy2023.modules.debugModule
import miragefairy2023.modules.demonBlockModule
import miragefairy2023.modules.demonItemModule
import miragefairy2023.modules.dreamCatcherModule
import miragefairy2023.modules.fairy.fairyModule
import miragefairy2023.modules.fairyCrystalGlassModule
import miragefairy2023.modules.mirageFlourModule
import miragefairy2023.modules.mirageFlowerModule
import miragefairy2023.modules.passiveskill.passiveSkillModule

val modules = module {
    commonModule()
    demonItemModule()
    demonBlockModule()
    fairyCrystalGlassModule()
    mirageFlourModule()
    mirageFlowerModule()
    fairyModule()
    dreamCatcherModule()
    passiveSkillModule()
    debugModule()
}
