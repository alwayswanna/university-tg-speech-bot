package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.constants.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

var logger = KotlinLogging.logger {}

@Service
class TelegramBotService(
    private val telegramUserService: TelegramUserService,
    private val botSettingService: BotSettingService,
) {

    fun invokeUpdate(update: Update): SendMessage {
        logger.info { "received update is null: $update" }

        /* extract info from user request */
        val message = update.message
        val chatIdFromUpdate = message?.chatId ?: update.callbackQuery.message.chatId
        val from = message?.from ?: update.callbackQuery.from
        val messageOrCallBackText = message?.text ?: update.callbackQuery.data


        /* handle /start - command */
        if (message != null && START == messageOrCallBackText) {
            return botSettingService.handleStartMessage(chatIdFromUpdate, from)
        }

        val userFromDatabase = telegramUserService.findUserByChatIdOrCreateNewUser(chatIdFromUpdate, from)
        if (validateMessageOnSettingStep(messageOrCallBackText)) {
            return botSettingService.handleSettingBranch(messageOrCallBackText, userFromDatabase, from)
        }

        if (EN_RESPONSE == messageOrCallBackText || LIST_USER_RESPONSE_VOICES.contains(messageOrCallBackText)) {
            return botSettingService.handleSettingBranch(messageOrCallBackText, userFromDatabase, from)
        }

        return buildDefaultResponse(chatIdFromUpdate)
    }

    private fun validateMessageOnSettingStep(text: String): Boolean {
        return if (text == EN_LOCALE || text == RU_LOCALE || YANDEX_VOICES.contains(text)) {
            TRUE
        } else {
            FALSE
        }
    }


    /**
     * Method sends default response. When we dont have solution for user message.
     */
    fun buildDefaultResponse(chatIdFromUpdate: Long): SendMessage {
        val defaultResponse = SendMessage()
        defaultResponse.apply {
            text = "Ох, кажется что то пошло не так. \n "
            chatId = chatIdFromUpdate.toString()
        }
        return defaultResponse
    }
}