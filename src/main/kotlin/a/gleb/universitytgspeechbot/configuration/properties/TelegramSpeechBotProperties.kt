package a.gleb.universitytgspeechbot.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("speech-synthesizer-bot")
data class TelegramSpeechBotProperties(
    var telegramApi: TelegramApi,
)

@ConstructorBinding
data class TelegramApi(
    var botToken: String,
    var botUsername: String
)

@ConstructorBinding
data class YandexApi(
    var token: String
)