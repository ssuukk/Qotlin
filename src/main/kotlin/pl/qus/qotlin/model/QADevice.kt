package pl.qus.qotlin.model

import pl.qus.qotlin.Qotlin
import java.util.Date

class QADevice(var name: String) {
    var status: QADeviceStatus? = null
    var serialNumber: String = ""
    var description: String = ""
    var id: String = ""
    var topologyId: String = ""
    var simulator: Boolean = false
    var nQubits: Int = 0
    //var couplingMap: Array<IntArray>? = null // TODO - can't decode as it is either array or string

    var chipName: String = ""
    var onlineDate: Date? = null
    var gateSet: String = ""
    var basisGates: String = ""
    var version: String = ""
    var url: String = ""
    var allowQObject: Boolean = false

    @Transient
    var api: Qotlin? = null

    override fun toString(): String {
        return "$name - $description, status: $status, real:${!simulator}, qbits:$nQubits, gates:$basisGates"
    }

    fun submitJob(shots: Int = 1, maxCredits: Int = 1, vararg sources: QAsm): QAJob {
        val job = QAJob(backend = this,shots = shots, maxCredits = maxCredits, qasms = listOf(*sources))
        return api?.submitJob(job)?.apply {
            this.api = this@QADevice.api
        } ?: throw(IllegalStateException("You have to obtain device instance from Qotlin instance"))
    }

}

enum class QADeviceStatus {
    on, off, standby
}