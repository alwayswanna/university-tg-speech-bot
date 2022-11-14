package a.gleb.universitytgspeechbot.feign

import a.gleb.universitytgspeechbot.models.yandex.YandexSpeechRequest
import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader


@FeignClient(
    name = "yandex-speech-api",
    url = "\${speech-synthesizer-bot.yandex-speech-api.url}"
)
interface YandexSpeechApiFeignClient {

    @PostMapping(
        "\${speech-synthesizer-bot.yandex-speech-api.path}",
        consumes = [APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun getVoice(
        @RequestHeader(value = "Authorization", required = true) authorizationHeader: String,
        yandexSpeechRequest: YandexSpeechRequest
    ): Response
}