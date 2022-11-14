package a.gleb.universitytgspeechbot.db.dao

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "iam_token")
data class IamToken(

    @field:Id
    @GeneratedValue(strategy = AUTO)
    var id: UUID? = null,

    @Column(name = "token")
    var token: String,

    @Column(name = "expired_date")
    var expiredDate: LocalDateTime? = null,

    @Column(name = "create_date")
    var createAt: LocalDateTime
) {
    /* No-args constructor for Spring */
    constructor() : this(null, "", null, LocalDateTime.now())
}
