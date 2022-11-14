package a.gleb.universitytgspeechbot.db.repository

import a.gleb.universitytgspeechbot.db.dao.IamToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface IamTokenRepository : JpaRepository<IamToken, UUID> {

    //TODO: check request yesterday
    fun findByExpiredDateAfterOrderByCreateAtAsc(currentTime: LocalDateTime): IamToken?
}