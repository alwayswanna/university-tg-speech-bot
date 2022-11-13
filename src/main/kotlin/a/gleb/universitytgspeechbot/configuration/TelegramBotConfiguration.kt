package a.gleb.universitytgspeechbot.configuration

import a.gleb.universitytgspeechbot.bot.TelegramSpeechBot
import a.gleb.universitytgspeechbot.exception.BotRegistrationException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotConfiguration {

    @Bean
    fun telegramBotsApi(bot: TelegramSpeechBot): TelegramBotsApi {
        try {
            val botAPI = TelegramBotsApi(DefaultBotSession::class.java)
            botAPI.registerBot(bot)
            return botAPI
        } catch (e: Exception) {
            throw BotRegistrationException(
                "Error while register telegram bot, message: ${e.message}"
            )
        }
    }

}