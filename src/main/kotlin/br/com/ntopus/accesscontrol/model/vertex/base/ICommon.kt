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
    var code: String = properties["code"].toString()

    @SerializedName("enable")
    var enable: Boolean = if (properties["enable"] != null) properties["enable"]!!.toBoolean() else true

    @SerializedName("name")
    var name: String = properties["name"].toString()
}