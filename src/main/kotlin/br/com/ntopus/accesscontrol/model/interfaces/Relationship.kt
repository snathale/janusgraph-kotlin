package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import org.janusgraph.core.EdgeLabel

data class VertexInfo(val label: String, val code: String, val edgeLabel: String? = "")
interface Relationship {
    fun createEdge(target: VertexInfo): JSONResponse
}