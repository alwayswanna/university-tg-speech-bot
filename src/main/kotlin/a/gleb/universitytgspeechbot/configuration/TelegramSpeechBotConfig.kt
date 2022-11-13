package a.gleb.universitytgspeechbot.configuration

import a.gleb.universitytgspeechbot.configuration.properties.TelegramSpeechBotProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TelegramSpeechBotProperties::class)
class TelegramSpeechBotConfig { }