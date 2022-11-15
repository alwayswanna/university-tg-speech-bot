package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.configuration.properties.TelegramSpeechBotProperties
import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import a.gleb.universitytgspeechbot.feign.YandexSpeechApiFeignClient
import a.gleb.universitytgspeechbot.models.BotSettingModel
import a.gleb.universitytgspeechbot.models.yandex.YandexSpeechRequest
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendVoice
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.User

private val logger = KotlinLogging.logger {}

const val BEARER = "Bearer "

@Service
class YandexSpeechService(
    private val iamYandexService: IamYandexService,
    private val objectMapper: ObjectMapper,
    private val yandexSpeechApiFeignClient: YandexSpeechApiFeignClient,
    private val properties: TelegramSpeechBotProperties
) {

    fun sendVoice(messageToConvert: String, from: User, userFromDatabase: TelegramUser): SendVoice {
        val iamToken = iamYandexService.getIamToken()
        logger.info { "iam token is: ${iamToken.token}" }
        val botSetting = extractBotSettings(userFromDatabase)

        val speechRequest =
            YandexSpeechRequest(
                messageToConvert,
                botSetting.language,
                botSetting.voice,
                properties.yandexApi.folderId
            )
        val speech = yandexSpeechApiFeignClient.getVoice(BEARER + iamToken.token, speechRequest)


        logger.info { "response: $speech" }
        return SendVoice().apply {
            chatId = userFromDatabase.chatTelegramId.toString()
            voice = InputFile(speech.body().asInputStream(), "response")
        }
    }

    /**
     * Convert string to object
     */
    fun extractBotSettings(telegramUser: TelegramUser): BotSettingModel {
        return objectMapper.readValue(telegramUser.botSettings, BotSettingModel::class.java)
    }
}