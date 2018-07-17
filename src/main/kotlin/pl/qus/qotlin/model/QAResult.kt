package pl.qus.qotlin.model

import com.google.gson.annotations.SerializedName

import java.util.Date


class QAResult {
    var date: Date? = null
    var data: QAData? = null

    override fun toString(): String {
        return "$date\n$data"
    }
}

class QAData {
    @SerializedName("creg_labels")
    var cregLabels: String = ""
    var additionalData: QAAdditional? = null
    var time: Double = 0.toDouble()
    var counts: Map<String, Int>? = null

    override fun toString(): String {
        return "$cregLabels time=$time counts=$counts"
    }
}

class QAAdditional {
    internal var seed: Long = 0
}
