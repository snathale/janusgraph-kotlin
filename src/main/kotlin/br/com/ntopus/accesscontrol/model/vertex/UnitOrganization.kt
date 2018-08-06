package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.data.*
import br.com.ntopus.accesscontrol.model.interfaces.Relationship
import br.com.ntopus.accesscontrol.model.vertex.base.*
import sun.security.provider.certpath.Vertex
import java.util.*

class UnitOrganization(properties: Map<String, String>): CommonAgent(properties), Relationship {
    override fun findByCode(code: String): Common {
        val g = graph.traversal()
        val values = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
                .has(PropertyLabel.CODE.label, this.code).valueMap<Vertex>()
        for (item in values) {
            this.name = item.get("name").toString()
            this.code = item.get("code").toString()
            this.enable = item.get("enable") as Boolean
            this.observation = item.get("observation").toString()
            this.creationDate = item.get("creationDate") as Date
        }
        return this
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        if (!target.label.equals(VertexLabel.GROUP.label)) {
            return FAILResponse(data = "Impossible create this edge $target from UnitOrganization")
        }

        val g = graph.traversal()
        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, this.code)
        if ( unitOrganization == null) {
            return FAILResponse(data = "Impossible find Unit Organization $this")
        }

        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, target.code)
        if (group == null) {
            return FAILResponse(data = "Impossible find Group $target")
        }

        try {
            unitOrganization.addE(EdgeLabel.HAS.label).to(group).next()
            graph.tx().commit()
        } catch (e: Exception) {
            g.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun insert(): JSONResponse {
        try {
            val unitOrganization = graph.addVertex("unitOrganization")
            unitOrganization.property("name", this.name)
            unitOrganization.property("code", this.code)
            unitOrganization.property("observation", this.observation)
            unitOrganization.property("creationDate", Date())
            unitOrganization.property("enable", true)
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