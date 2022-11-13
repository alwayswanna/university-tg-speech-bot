package a.gleb.universitytgspeechbot.service

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service

/**
 * Service returns responses for bot.
 */
@Service
class BotMessageService(
    private var messageSource: MessageSource
) {

    fun getMessage(key: String, @Nullable args: Array<Any?>?, localeCode: String): String {
        val localePrefix = getLocale(localeCode)
        return messageSource.getMessage(localePrefix + key, args, LocaleContextHolder.getLocale())
    }

    fun getMessage(key: String, localeCode: String): String {
        val localePrefix = getLocale(localeCode)
        return messageSource.getMessage(localePrefix + key, null, LocaleContextHolder.getLocale())
    }

    private fun getLocale(localeCode: String): String {
        return if (localeCode == "ru") {
            "ru."
        } else {
            "en."
        }
    }
}
