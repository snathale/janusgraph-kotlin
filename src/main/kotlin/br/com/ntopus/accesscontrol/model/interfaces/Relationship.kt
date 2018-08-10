package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo

interface Relationship {
    fun createEdge(target: VertexInfo): JSONResponse
}