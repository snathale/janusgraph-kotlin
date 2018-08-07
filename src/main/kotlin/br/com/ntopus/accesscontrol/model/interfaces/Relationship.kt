package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse

data class VertexInfo(val label: String, val code: String)
interface Relationship {
    fun createEdge(target: VertexInfo): JSONResponse
}