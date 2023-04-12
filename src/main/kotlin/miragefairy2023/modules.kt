package miragefairy2023

import miragefairy2023.modules.commonModule
import miragefairy2023.modules.debugModule
import miragefairy2023.modules.demonItemModule
import miragefairy2023.modules.dreamCatcherModule
import miragefairy2023.modules.fairyModule
import miragefairy2023.modules.mirageFlourModule
import miragefairy2023.modules.mirageFlowerModule
import miragefairy2023.modules.passiveskill.passiveSkillModule

val modules = module {
    commonModule()
    demonItemModule()
    mirageFlourModule()
    mirageFlowerModule()
    fairyModule()
    dreamCatcherModule()
    passiveSkillModule()
    debugModule()
}
