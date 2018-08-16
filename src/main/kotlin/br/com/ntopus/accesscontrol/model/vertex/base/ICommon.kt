package br.com.ntopus.accesscontrol.model.vertex.base

import br.com.ntopus.accesscontrol.model.StatusResponse
import com.google.gson.annotations.SerializedName

interface JSONResponse {
    val status: String
}

data class SUCCESSResponse(override val status: String = StatusResponse.SUCCESS.toString(), val data: Any?): JSONResponse
data class FAILResponse(override val status: String = StatusResponse.FAIL.toString(), val data: Any): JSONResponse
data class ERRORResponse(override val status: String = StatusResponse.ERROR.toString(), val message: String): JSONResponse
abstract class ICommon(properties: Map<String, String>) {

    @SerializedName("code")
    var code: String = this.toString(properties["code"])

    @SerializedName("enable")
    var enable: Boolean = if (!this.toString(properties["enable"]).isEmpty()) properties["enable"]!!.toBoolean() else true

    fun toString(value: Any?): String {
        if (value.toString() == "null") {
            return ""
        }
        return value.toString()
    }

    abstract fun formatDate(): String?
}