package a.gleb.universitytgspeechbot.models

import a.gleb.universitytgspeechbot.constants.EN_LOCALE
import a.gleb.universitytgspeechbot.constants.JOHN_VOICE
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Default values for international usage.
 */
data class BotSettingModel(
    @get:JsonProperty("language") var language: String = EN_LOCALE,
    @get:JsonProperty("voice") var voice: String = JOHN_VOICE
)
