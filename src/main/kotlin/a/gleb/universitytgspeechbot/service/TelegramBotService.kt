package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.constants.*
import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

private val logger = KotlinLogging.logger {}

@Service
class TelegramBotService(
    private val telegramUserService: TelegramUserService,
    private val botSettingService: BotSettingService,
    private val yandexSpeechService: YandexSpeechService,
) {

    fun invokeUpdate(update: Update): PartialBotApiMethod<Message> {
        logger.info { "received update is null: $update" }

        /* extract info from user request */
        val message = update.message
        val chatIdFromUpdate = message?.chatId ?: update.callbackQuery.message.chatId
        val from = message?.from ?: update.callbackQuery.from
        val messageOrCallBackText = message?.text ?: update.callbackQuery.data
        val userFromDatabase = telegramUserService.findUserByChatIdOrCreateNewUser(chatIdFromUpdate, from)

        if (LIST_SETTINGS.contains(messageOrCallBackText)) {
            return botSettingService.handleSettingsMessage(
                chatIdFromUpdate,
                userFromDatabase,
                from,
                messageOrCallBackText
            )
        }

        if (START == messageOrCallBackText || userFromDatabase.isReady == false ||
            LIST_USER_RESPONSE_VOICES.contains(messageOrCallBackText) || RU_LOCALE == messageOrCallBackText ||
            EN_LOCALE == messageOrCallBackText
        ) {
            return isNotReadyToUse(chatIdFromUpdate, from, messageOrCallBackText, userFromDatabase)
        }


        return if (userFromDatabase.isReady == true) {
            yandexSpeechService.sendVoice(messageOrCallBackText, from, userFromDatabase)
        } else {
            buildDefaultResponse(chatIdFromUpdate)
        }
    }

    private fun isNotReadyToUse(
        chatIdFromUpdate: Long,
        from: User,
        messageOrCallBackText: String,
        userFromDatabase: TelegramUser
    ): SendMessage {
        /* handle /start - command */
        return if (START == messageOrCallBackText) {
            botSettingService.handleStartMessage(chatIdFromUpdate, from)
        } else if (validateMessageOnSettingStep(messageOrCallBackText)) {
            botSettingService.handleSettingBranch(messageOrCallBackText, userFromDatabase, from)
        } else if (EN_RESPONSE == messageOrCallBackText || LIST_USER_RESPONSE_VOICES.contains(messageOrCallBackText)) {
            botSettingService.handleSettingBranch(messageOrCallBackText, userFromDatabase, from)
        } else {
            buildDefaultResponse(chatIdFromUpdate)
        }
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
            text =
                "Ох, кажется что то пошло не так. Вероятно вы настроили бота до конца или настроили неправильно. \n" +
                        "Oh, looks like something went wrong. You probably configured the bot to the end or configured it incorrectly."
            chatId = chatIdFromUpdate.toString()
        }
        return defaultResponse
    }
}