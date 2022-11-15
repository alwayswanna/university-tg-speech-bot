package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.constants.*
import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import a.gleb.universitytgspeechbot.exception.InvalidBotCommandException
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

const val VOICE = "Voice - \uD83C\uDFB5"
const val LANGUAGE = "Language - \uD83C\uDDF7\uD83C\uDDFA / \uD83C\uDDFA\uD83C\uDDF8"

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
        val username = from.userName ?: EMPTY
        val helloMessage = SendMessage().apply {
            text = messageService.getMessage(
                "init.message",
                arrayOf(username),
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

    fun handleSettingsMessage(
        chatIdFromUpdate: Long,
        telegramUser: TelegramUser,
        from: User,
        messageOrCallback: String
    ): SendMessage {
        return when (messageOrCallback) {
            SETTINGS -> settingsMain(from, telegramUser)
            VOICE_SETTINGS -> handleChangeVoice(messageOrCallback, telegramUser, from)
            LANGUAGE_SETTINGS -> handleChangeLanguage(from, telegramUser)
            else -> {
                throw InvalidBotCommandException("Invalid command from user, $messageOrCallback")
            }
        }
    }

    /**
     * Method handle /settings command from bot.
     */
    private fun settingsMain(from: User, telegramUser: TelegramUser): SendMessage {
        return SendMessage().apply {
            text = messageService.getMessage(
                "settings.message",
                from.languageCode
            )
            chatId = telegramUser.chatTelegramId.toString()
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = settingsKeyBoardsSetup()
            }
        }
    }

    /**
     * Method handle /language command from user.
     */
    fun handleChangeLanguage(from: User, telegramUser: TelegramUser): SendMessage {
        return SendMessage().apply {
            text = messageService.getMessage(
                "settings.language",
                from.languageCode
            )
            chatId = telegramUser.chatTelegramId.toString()
            replyMarkup = InlineKeyboardMarkup().apply {
                keyboard = languageKeyBoardsSetup()
            }
        }
    }

    /**
     * Method handle /voice command from user.
     */
    fun handleChangeVoice(messageOrCallback: String, telegramUser: TelegramUser, from: User): SendMessage {
        val botSettings = extractBotSettings(telegramUser)
        if (botSettings.language == RU_LOCALE) {
            return SendMessage().apply {
                text = messageService.getMessage(
                    "settings.voice",
                    from.languageCode
                )
                replyMarkup = ReplyKeyboardMarkup().apply {
                    keyboard = voicesKeyBoardSetup(RU_LOCALE)
                }
                chatId = telegramUser.chatTelegramId.toString()
            }
        } else {
            return SendMessage().apply {
                text = messageService.getMessage(
                    "settings.voice",
                    from.languageCode
                )
                replyMarkup = ReplyKeyboardMarkup().apply {
                    keyboard = voicesKeyBoardSetup(EN_LOCALE)
                }
                chatId = telegramUser.chatTelegramId.toString()
            }
        }
    }

    /**
     * Method handle setup branch for bot.
     */
    fun handleSettingBranch(messageOrCallback: String, user: TelegramUser, from: User): SendMessage {
        when (messageOrCallback) {
            RU_LOCALE -> return updateSettingsLocal(messageOrCallback, user, from)
            EN_LOCALE -> return updateSettingsLocal(messageOrCallback, user, from)
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

    fun settingsKeyBoardsSetup(): List<List<InlineKeyboardButton>> {
        return listOf(
            listOf(
                InlineKeyboardButton().apply {
                    text = VOICE
                    callbackData = VOICE_SETTINGS
                },
                InlineKeyboardButton().apply {
                    text = LANGUAGE
                    callbackData = LANGUAGE_SETTINGS
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
                isReady = false
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
        val voiceFromMessage: String = if (message.contains(EN_EMOJI)) {
            message.replace(EN_EMOJI, "")
        } else {
            message.replace(RU_EMOJI, "")
        }


        val botSettingModel = extractBotSettings(user)
        val voiceToSave = YANDEX_VOICES_MAP[voiceFromMessage.replace(SPACE, EMPTY)]
            ?: validateVoice(botSettingModel.language)

        botSettingModel.voice = voiceToSave

        user.apply {
            apply {
                botSettings = objectMapper.writeValueAsString(botSettingModel)
                lastUpdate = LocalDateTime.now()
                isReady = true
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
            BotSettingModel()
        }
        return botSetting
    }

    fun validateVoice(voice: String): String {
        return if (voice == "ru-Ru") {
            ALENA_VOICE
        } else {
            JOHN_VOICE
        }
    }
}