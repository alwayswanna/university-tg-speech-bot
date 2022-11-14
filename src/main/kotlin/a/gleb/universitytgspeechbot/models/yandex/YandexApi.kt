package a.gleb.universitytgspeechbot.models.yandex

import java.time.LocalDateTime

data class IamYandexResponse(
    var iamToken: String,
    var expiresAt: LocalDateTime
)

data class YamYandexRequest(
    var yandexPassportOauthToken: String
)

data class YandexSpeechRequest(
    var text: String,
    var lang: String,
    var voice: String,
    var folderId: String
)
