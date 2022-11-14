package a.gleb.universitytgspeechbot.feign

import a.gleb.universitytgspeechbot.models.yandex.IamYandexResponse
import a.gleb.universitytgspeechbot.models.yandex.YamYandexRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(
    name = "yandex-iam-api",
    url = "\${speech-synthesizer-bot.yandex-iam-api.url}"
)
interface YandexApiIamFeignClient {

    @PostMapping("\${speech-synthesizer-bot.yandex-iam-api.path}")
    fun getIamToken(iamYandexRequest: YamYandexRequest): IamYandexResponse
}