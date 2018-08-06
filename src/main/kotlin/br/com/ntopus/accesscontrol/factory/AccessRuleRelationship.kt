package br.com.ntopus.accesscontrol.factory

import br.com.ntopus.accesscontrol.model.data.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse

interface AccessRuleRelationship {
    fun createEdgeProvide(source: VertexInfo, target: VertexInfo): JSONResponse
    fun createEdgeOwn(source: VertexInfo, target: VertexInfo): JSONResponse
}