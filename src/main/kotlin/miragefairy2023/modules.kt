package miragefairy2023

import miragefairy2023.modules.ParticleModule
import miragefairy2023.modules.TelescopeModule
import miragefairy2023.modules.advancementModule
import miragefairy2023.modules.commonModule
import miragefairy2023.modules.customDataModule
import miragefairy2023.modules.debugModule
import miragefairy2023.modules.demonBlockModule
import miragefairy2023.modules.demonItemModule
import miragefairy2023.modules.dreamCatcherModule
import miragefairy2023.modules.fairy.fairyModule
import miragefairy2023.modules.fairyCrystalGlassModule
import miragefairy2023.modules.fairyhouse.fairyHouseModule
import miragefairy2023.modules.mirageFlourModule
import miragefairy2023.modules.mirageFlowerModule
import miragefairy2023.modules.passiveskill.passiveSkillModule

val modules = module {
    commonModule()
    advancementModule()
    demonItemModule()
    demonBlockModule()
    fairyCrystalGlassModule()
    TelescopeModule.init(this)
    fairyHouseModule()
    mirageFlourModule()
    mirageFlowerModule()
    fairyModule()
    dreamCatcherModule()
    passiveSkillModule()
    debugModule()
    customDataModule()
    ParticleModule.init(this)
}
