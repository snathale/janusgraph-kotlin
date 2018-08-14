package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.*
import br.com.ntopus.accesscontrol.model.vertex.base.*
import sun.security.provider.certpath.Vertex
import java.util.*

class UnitOrganization(properties: Map<String, String>): IAgent(properties) {
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

}