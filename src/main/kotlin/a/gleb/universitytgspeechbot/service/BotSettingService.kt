package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.constants.EN_LOCALE
import a.gleb.universitytgspeechbot.constants.RU_LOCALE
import a.gleb.universitytgspeechbot.constants.YANDEX_RUSSIAN_VOICES
import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import a.gleb.universitytgspeechbot.models.BotSettingModel
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import java.time.LocalDateTime

const val RU_EMOJI = "\uD83C\uDDF7\uD83C\uDDFA"
const val RU_LOCALE_TEXT = "$RU_EMOJI - Русский"

const val EN_EMOJI = "\uD83C\uDDFA\uD83C\uDDF8"
const val EN_LOCAL_TEXT = "$EN_EMOJI - English"

@Service
class BotSettingService(
    private val messageService: BotMessageService,
    private val telegramUserService: TelegramUserService,
    private val objectMapper: ObjectMapper
) {

    /**
     * Handle start message from user.
     */
    fun handleStartMessage(chatIdFromUpdate: Long, from: User): SendMessage {
        val helloMessage = SendMessage().apply {
            text = messageService.getMessage(
                "init.message",
                arrayOf(from.userName.toString()),
                from.languageCode
            )
            chatId = chatIdFromUpdate.toString()
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = languageKeyBoardsSetup()
            }
        }

        telegramUserService.findUserByChatIdOrCreateNewUser(chatIdFromUpdate, from)
        return helloMessage
    }

    /**
     * Method handle setup branch for bot.
     */
    fun handleSettingBranch(messageOrCallback: String, user: TelegramUser, from: User): SendMessage {
        when (messageOrCallback) {
            "ru-RU" -> return updateSettingsLocal(messageOrCallback, user, from)
            "en-US" -> return updateSettingsLocal(messageOrCallback, user, from)
        }
        return updateSettingsVoice(messageOrCallback, user, from)
    }

    /**
     * Method build keyboard for chose user language.
     */
    fun languageKeyBoardsSetup(): List<List<InlineKeyboardButton>> {
        return listOf(
            listOf(
                InlineKeyboardButton().apply {
                    text = RU_LOCALE_TEXT
                    callbackData = RU_LOCALE
                },
                InlineKeyboardButton().apply {
                    text = EN_LOCAL_TEXT
                    callbackData = EN_LOCALE
                })
        )
    }

    /**
     * Method update setting for Yandex API. (Language)
     */
    private fun updateSettingsLocal(message: String, user: TelegramUser, from: User): SendMessage {
        val botSettingModel = extractBotSettings(user)
        botSettingModel.language = message

        user.apply {
            apply {
                botSettings = objectMapper.writeValueAsString(botSettingModel)
                lastUpdate = LocalDateTime.now()
            }
        }
        telegramUserService.save(user)

        return SendMessage().apply {
            chatId = user.chatTelegramId.toString()
            text = messageService.getMessage("voice.message", from.languageCode)
            replyMarkup = ReplyKeyboardMarkup().apply {
                keyboard = voicesKeyBoardSetup(message)
            }
        }
    }

    /**
     * Method update setting for Yandex API. (Voice)
     */
    private fun updateSettingsVoice(message: String, user: TelegramUser, from: User): SendMessage {
        if (message.contains(EN_EMOJI)) {
            message.replace(EN_EMOJI, "")
        } else {
            message.replace(RU_EMOJI, "")
        }
        message.replace(" ", "")


        val botSettingModel = extractBotSettings(user)
        botSettingModel.voice = message

        user.apply {
            apply {
                botSettings = objectMapper.writeValueAsString(botSettingModel)
                lastUpdate = LocalDateTime.now()
            }
        }
        telegramUserService.save(user)

        return SendMessage().apply {
            chatId = user.chatTelegramId.toString()
            text = messageService.getMessage(
                "setup.finish",
                arrayOf(botSettingModel.voice, botSettingModel.language),
                from.languageCode
            )
        }
    }

    /**
     * Method create keyboard for choose voice for Yandex API.
     */
    private fun voicesKeyBoardSetup(locale: String): List<KeyboardRow> {
        if (locale == EN_LOCALE) {
            val row = KeyboardRow()
            row.add(KeyboardButton().apply {
                text = "John $EN_EMOJI"
            })
            return listOf(row)
        } else {
            return YANDEX_RUSSIAN_VOICES.asSequence()
                .map {
                    val row = KeyboardRow()
                    row.add(KeyboardButton().apply {
                        text = "${it.value} $RU_EMOJI"
                    })
                    row
                }
                .toList()
        }
    }

    /**
     * Convert string to object
     */
    fun extractBotSettings(telegramUser: TelegramUser): BotSettingModel {
        val botSetting: BotSettingModel = if (telegramUser.botSettings != null) {
            objectMapper.readValue(telegramUser.botSettings, BotSettingModel::class.java)
        } else {
            BotSettingModel(null, null)
        }
        return botSetting
    }
}