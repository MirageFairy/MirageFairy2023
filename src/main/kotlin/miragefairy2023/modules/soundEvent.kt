package miragefairy2023.modules

import miragefairy2023.MirageFairy2023
import miragefairy2023.SoundsProvider
import miragefairy2023.module
import miragefairy2023.util.init.Translation
import miragefairy2023.util.init.translation
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

enum class DemonSoundEventCard(val path: String, en: String, ja: String, soundPaths: List<String>) {
    MAGIC1("magic1", "Magic fired", "魔法が発射される", listOf("magic1")),
    ;

    val identifier = Identifier(MirageFairy2023.modId, path);
    val sounds = soundPaths.map { Identifier(MirageFairy2023.modId, it) }
    val translation = Translation(identifier.toTranslationKey("subtitles"), en, ja)
    val soundEvent = SoundEvent(identifier);
}

val soundEventModule = module {
    DemonSoundEventCard.values().forEach { card ->
        Registry.register(Registry.SOUND_EVENT, card.identifier, card.soundEvent)
        onGenerateSounds { it.map[card.path] = SoundsProvider.SoundEntry(card.translation.key, card.sounds) }
        translation(card.translation)
    }
}
