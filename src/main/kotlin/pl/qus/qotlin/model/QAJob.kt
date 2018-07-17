package pl.qus.qotlin.model

import pl.qus.qotlin.Qotlin
import java.util.*

open class QAJob (
        var backend: QADevice? = null,
        var shots: Int = 0,
        var maxCredits: Int = 0,
        val qasms : List<QAsm>? = null,
        val status : StatusEnum? = null,
        val usedCredits : Int = 0,
        val creationDate : Date? = null,
        val id : String = "",
        val userId : String = "",
        var deleted: Boolean = false,
        var calibration: QACalibration? = null,
        var error : QAError? = null,
        @Transient
        var api: Qotlin? = null
) {
    override fun toString(): String {
        return "$backend, id:$id, status:$status"
    }

    fun onStatus(timeoutSeconds : Int,onCompleted: (QAJob) -> Unit,
                 onError: (Throwable) -> Unit = {}) {
        api?.onJobStatus(this,timeoutSeconds,onCompleted,onError) ?: throw(IllegalStateException("You have to obtain QAJob instance from submitJob method"))
    }
}

enum class StatusEnum {
    RUNNING, COMPLETED
}