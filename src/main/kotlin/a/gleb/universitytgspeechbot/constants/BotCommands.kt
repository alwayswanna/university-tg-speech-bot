package a.gleb.universitytgspeechbot.constants

const val COMMAND_INIT_CHAR: String = "/"

const val START: String = COMMAND_INIT_CHAR + "start"

const val STOP: String = COMMAND_INIT_CHAR + "stop"

const val RU_LOCALE = "ru-RU"

const val EN_LOCALE = "en-US"

const val EN_EMOJI = "\uD83C\uDDFA\uD83C\uDDF8"

const val EN_RESPONSE = "John $EN_EMOJI"

const val RU_EMOJI = "\uD83C\uDDF7\uD83C\uDDFA"

val LIST_USER_RESPONSE_VOICES = listOf(
    "Алена $RU_EMOJI",
    "Филип $RU_EMOJI",
    "Ермил $RU_EMOJI",
    "Жанна $RU_EMOJI",
    "Мадирус $RU_EMOJI",
    "Омаж $RU_EMOJI",
    "Захар $RU_EMOJI"
)