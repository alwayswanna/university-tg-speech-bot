package a.gleb.universitytgspeechbot.configuration

import a.gleb.universitytgspeechbot.configuration.properties.TelegramSpeechBotProperties
import a.gleb.universitytgspeechbot.feign.YandexApiIamFeignClient
import a.gleb.universitytgspeechbot.feign.YandexSpeechApiFeignClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TelegramSpeechBotProperties::class)
@EnableFeignClients(
    basePackageClasses = [
        YandexApiIamFeignClient::class,
        YandexSpeechApiFeignClient::class
    ]
)
class TelegramSpeechBotConfig {}