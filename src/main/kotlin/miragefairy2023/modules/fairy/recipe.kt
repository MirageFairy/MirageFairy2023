package miragefairy2023.modules.fairy

import miragefairy2023.InitializationScope

class FairyRecipes {
    val recipes = mutableListOf<FairyRecipe>()
}

interface FairyRecipe {
    fun getWikiString(): String
    fun init(initializationScope: InitializationScope, fairyCard: FairyCard)
}
