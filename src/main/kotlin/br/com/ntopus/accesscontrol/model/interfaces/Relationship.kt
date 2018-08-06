package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.data.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse

interface Relationship {
    fun createEdge(target: VertexInfo): JSONResponse
}