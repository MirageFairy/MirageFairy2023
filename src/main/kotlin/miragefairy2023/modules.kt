package miragefairy2023

import miragefairy2023.modules.advancementModule
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
import miragefairy2023.modules.telescopeModule

val modules = module {
    commonModule()
    advancementModule()
    demonItemModule()
    demonBlockModule()
    fairyCrystalGlassModule()
    telescopeModule()
    mirageFlourModule()
    mirageFlowerModule()
    fairyModule()
    dreamCatcherModule()
    passiveSkillModule()
    debugModule()
}
