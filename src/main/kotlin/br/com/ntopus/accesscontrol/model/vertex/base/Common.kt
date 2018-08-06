package br.com.ntopus.accesscontrol.model.vertex.base

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.StatusResponse

interface JSONResponse {
    val status: String
}

data class SUCCESSResponse(override val status: String = StatusResponse.SUCCESS.toString(), val data: Any): JSONResponse
data class FAILResponse(override val status: String = StatusResponse.FAIL.toString(), val data: Any): JSONResponse
data class ERRORResponse(override val status: String = StatusResponse.ERROR.toString(), val message: String): JSONResponse
abstract class Common(properties: Map<String, String>) {

    val graph = GraphFactory.open()

    var code: String = properties["code"].toString()
    var enable: Boolean = if (properties["enable"] != null) properties["enable"]!!.toBoolean() else true

    abstract fun insert (): JSONResponse

    abstract fun findByCode (code: String): Common
}