package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.data.*
import br.com.ntopus.accesscontrol.model.interfaces.Relationship
import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import java.util.*

class Organization(properties: Map<String, String>): CommonAgent(properties), Relationship {
    override fun createEdge(source: VertexInfo, target: VertexInfo): JSONResponse {
        if (!target.label.equals(VertexLabel.UNIT_ORGANIZATION.label)) {
            return FAILResponse(data = "Impossible create this edge $target from Organization")
        }

        val g = graph.traversal()
        val organization = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, source.code)
        if ( organization == null) {
            return FAILResponse(data = "Impossible find this Organization $source")
        }

        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, target.code)
        if (unitOrganization == null) {
            return FAILResponse(data = "Impossible find this Unit Organization $target")
        }

        try {
            organization.addE(EdgeLabel.HAS.label).to(unitOrganization).next()
            graph.tx().commit()
        } catch (e: Exception) {
            g.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun insert(): JSONResponse {
        try {
            val organization = graph.addVertex("organization")
            organization.property("name", this.name)
            organization.property("code", this.code)
            organization.property("observation", this.observation)
            organization.property("creationDate", Date())
            organization.property("enable", true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

//    @Incidence(label = "has")
//    abstract override fun createEdge(vertex: Common)
}