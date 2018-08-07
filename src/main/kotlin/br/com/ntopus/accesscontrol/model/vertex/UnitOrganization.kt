package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.*
import br.com.ntopus.accesscontrol.model.interfaces.Relationship
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.*
import sun.security.provider.certpath.Vertex
import java.time.LocalDate
import java.util.*

class UnitOrganization(properties: Map<String, String>): ICommonAgent(properties) {
    companion object {
        fun findByCode(code: String): ICommon {
            val g = GraphFactory.open().traversal()
            val values = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
            val unitOrganization = UnitOrganization(hashMapOf())
            for (item in values) {
                unitOrganization.name = item.get("name").toString()
                unitOrganization.code = item.get("code").toString()
                unitOrganization.enable = item.get("enable") as Boolean
                unitOrganization.observation = item.get("observation").toString()
                unitOrganization.creationDate = item.get("creationDate") as Date
            }
            return unitOrganization
        }
    }

//    fun createEdge(target: VertexInfo) {
//        if (!target.label.equals(VertexLabel.GROUP.label)) {
//            return FAILResponse(data = "Impossible create this edge $target from UnitOrganization")
//        }
//
//        val g = graph.traversal()
//        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, this.code)
//        if ( unitOrganization == null) {
//            return FAILResponse(data = "Impossible find Unit Organization $this")
//        }
//
//        val group = g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, target.code)
//        if (group == null) {
//            return FAILResponse(data = "Impossible find Group $target")
//        }
//
//        try {
//            unitOrganization.addE(EdgeLabel.HAS.label).to(group).next()
//            graph.tx().commit()
//        } catch (e: Exception) {
//            g.tx().rollback()
//            return FAILResponse(data = e.message.toString())
//        }
//        return SUCCESSResponse(data = this)
//    }

}