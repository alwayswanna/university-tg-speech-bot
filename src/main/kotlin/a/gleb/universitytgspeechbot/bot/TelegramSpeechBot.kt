package a.gleb.universitytgspeechbot.bot

import a.gleb.universitytgspeechbot.configuration.properties.TelegramSpeechBotProperties
import a.gleb.universitytgspeechbot.service.TelegramBotService
import a.gleb.universitytgspeechbot.exception.BotExecuteCommandException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

var logger = KotlinLogging.logger {}

@Component
class TelegramSpeechBot(
    private var properties: TelegramSpeechBotProperties,
    private var telegramBotService: TelegramBotService
) : TelegramLongPollingBot() {


    override fun getBotToken(): String {
        return properties.telegramApi.botToken
    }

    override fun getBotUsername(): String {
        return properties.telegramApi.botUsername
    }

    override fun onUpdateReceived(update: Update?) {
        logger.info { "bot revived message: $update " }
        if (update != null) {
            val response = telegramBotService.invokeUpdate(update)
            try {
                execute(response)
            } catch (e: Exception) {
                throw BotExecuteCommandException(
                    "Error while execute command for chatId: ${response.chatId}, message: ${e.message}"
                )
            }
        }

    }

}