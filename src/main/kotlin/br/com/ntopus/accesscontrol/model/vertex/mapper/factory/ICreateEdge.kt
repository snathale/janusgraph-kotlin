package br.com.ntopus.accesscontrol.model.vertex.mapper.factory

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.structure.Vertex

abstract class ICreateEdge {
    val graph = GraphFactory.open()
    abstract fun createEdge(vSource: Vertex, vTarget: Vertex, target: VertexInfo, sourceCode: String): JSONResponse
}