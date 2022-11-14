package a.gleb.universitytgspeechbot.db.dao

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "bot_users")
data class TelegramUser(

    @field:Id
    @GeneratedValue(strategy = AUTO)
    var id: UUID? = null,

    @Column(name = "user_telegram_id")
    var userTelegramId: Long,

    @Column(name = "chat_telegram_id")
    var chatTelegramId: Long,

    // TODO: convert to jsonb type or migrate to another table.
    @Column(name = "bot_settings")
    var botSettings: String?,

    @Column(name = "last_update")
    var lastUpdate: LocalDateTime,

    @Column(name = "is_ready")
    var isReady: Boolean? = false
) {
    /* No-args constructor for Spring */
    constructor() : this(
        null, Long.MAX_VALUE, Long.MAX_VALUE, "{}", LocalDateTime.now(), true
    )
}