package a.gleb.universitytgspeechbot.db.repository

import a.gleb.universitytgspeechbot.db.dao.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface TelegramUserRepository : JpaRepository<TelegramUser, UUID> {

    fun findByUserTelegramId(id: Long): TelegramUser?
}