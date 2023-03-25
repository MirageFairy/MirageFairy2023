package miragefairy2023

import miragefairy2023.core.init.module
import miragefairy2023.core.init.modules.demonItemModule
import miragefairy2023.core.init.modules.fairyModule
import miragefairy2023.core.init.modules.mirageFlowerModule

val modules = module {
    demonItemModule()
    mirageFlowerModule()
    fairyModule()
}
