package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.AccessRule
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import org.janusgraph.core.JanusGraph

class AccessRuleMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    private val accessRule = AccessRule(properties)
    override fun insert(): JSONResponse {
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE)
            accessRule.property(PropertyLabel.CODE.label, this.accessRule.code)
            accessRule.property(PropertyLabel.ENABLE.label, this.accessRule.enable)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, this.accessRule.expirationDate)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun updateProperty(vertex: VertexInfo, property: Property): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(vertex: VertexInfo, code: String): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}