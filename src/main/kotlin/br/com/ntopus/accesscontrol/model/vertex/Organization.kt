package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.*
import br.com.ntopus.accesscontrol.model.interfaces.Relationship
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.time.LocalDate
import java.util.*

class Organization(properties: Map<String, String>): ICommonAgent(properties) {
    companion object {
        fun findByCode(code: String): ICommon {
            val g = GraphFactory.open().traversal()
            val values = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
            val unitOrganization = User(hashMapOf())
            for (item in values) {
                unitOrganization.name = item.get(PropertyLabel.NAME.label).toString()
                unitOrganization.code = item.get(PropertyLabel.CODE.label).toString()
                unitOrganization.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
                unitOrganization.observation = item.get(PropertyLabel.OBSERVATION.label).toString()
                unitOrganization.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
            }
            return unitOrganization
        }
    }

    fun createEdge(target: VertexInfo) {
//        if (!target.label.equals(VertexLabel.UNIT_ORGANIZATION.label)) {
//            return FAILResponse(data = "Impossible create this edge $target from Organization")
//        }
//
//        val g = graph.traversal()
//        val organization = g.V().hasLabel(VertexLabel.ORGANIZATION.label).has(PropertyLabel.CODE.label, this.code)
//        if ( organization == null) {
//            return FAILResponse(data = "Impossible find this Organization $this")
//        }
//
//        val unitOrganization = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, target.code)
//        if (unitOrganization == null) {
//            return FAILResponse(data = "Impossible find this Unit Organization $target")
//        }
//
//        try {
//            organization.addE(EdgeLabel.HAS.label).to(unitOrganization).next()
//            graph.tx().commit()
//        } catch (e: Exception) {
//            g.tx().rollback()
//            return FAILResponse(data = e.message.toString())
//        }
//        return SUCCESSResponse(data = this)
    }

//    @Incidence(label = "has")
//    abstract override fun createEdge(vertex: ICommon)
}