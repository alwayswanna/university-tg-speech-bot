package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.configuration.properties.TelegramSpeechBotProperties
import a.gleb.universitytgspeechbot.db.dao.IamToken
import a.gleb.universitytgspeechbot.db.repository.IamTokenRepository
import a.gleb.universitytgspeechbot.feign.YandexApiIamFeignClient
import a.gleb.universitytgspeechbot.models.yandex.YamYandexRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class IamYandexService(
    private val yandexApiIamFeignClient: YandexApiIamFeignClient,
    private val iamTokenRepository: IamTokenRepository,
    private val properties: TelegramSpeechBotProperties
) {

    /**
     * Method try get token from database, or make request for new token.
     */
    fun getIamToken(): IamToken {
        val currentDateTime = LocalDateTime.now()
        return iamTokenRepository.findByExpiredDateAfterOrderByCreateAtAsc(currentDateTime)
            ?: return getNewToken()
    }

    /**
     * Method make request for get new token.
     */
    fun getNewToken(): IamToken {
        val requestBody = YamYandexRequest(properties.yandexApi.token)
        val tokenResponse = yandexApiIamFeignClient.getIamToken(requestBody)
        return iamTokenRepository.save(IamToken().apply {
            token = tokenResponse.iamToken
            createAt = LocalDateTime.now()
            expiredDate = tokenResponse.expiresAt
        })
    }
}