package br.com.ntopus.accesscontrol.model.vertex.mapper.factory

import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.EdgeCreated
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.structure.Vertex

class EdgeFromAccessGroup: ICreateEdge() {
    override fun createEdge(vSource: Vertex, vTarget: Vertex, target: VertexInfo, sourceCode: String): JSONResponse {
        try {
            vSource.addEdge(EdgeLabel.INHERIT.label, vTarget)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCEE-003 ${e.message.toString()}")
        }
        val source = VertexInfo(VertexLabel.ACCESS_GROUP.label, sourceCode)
        return SUCCESSResponse(data = EdgeCreated(source, target, EdgeLabel.INHERIT.label))
    }
}