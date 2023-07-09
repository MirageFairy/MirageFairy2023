package miragefairy2023.util

import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.FeatureConfig

infix fun <C : FeatureConfig> Feature<C>.with(config: C) = ConfiguredFeature(this, config)
