package a.gleb.universitytgspeechbot.service

import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import a.gleb.universitytgspeechbot.db.repository.TelegramUserRepository
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import java.time.LocalDateTime

@Service
class TelegramUserService(
    private val telegramUserRepository: TelegramUserRepository
) {

    fun findUserByChatIdOrCreateNewUser(chatId: Long, from: User): TelegramUser {
        val user = telegramUserRepository.findByUserTelegramId(chatId)

        return if (user != null) {
            user
        } else {
            val tgUser = TelegramUser().apply {
                userTelegramId = from.id
                chatTelegramId = chatId
                lastUpdate = LocalDateTime.now()
                isReady = false
            }
            telegramUserRepository.save(tgUser)
        }
    }

    fun save(user: TelegramUser) {
        telegramUserRepository.save(user)
    }
}