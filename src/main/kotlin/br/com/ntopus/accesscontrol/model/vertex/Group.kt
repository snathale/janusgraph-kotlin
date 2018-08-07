package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.time.LocalDate
import java.util.*


class Group(properties: Map<String, String>): ICommonAgent(properties) {

    companion object {
        fun findByCode(code: String): ICommon {
            val g = GraphFactory.open().traversal()
            val values = g.V().hasLabel(VertexLabel.GROUP.label)
                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
            val group = User(hashMapOf())
            for (item in values) {
                group.name = item.get(PropertyLabel.NAME.label).toString()
                group.code = item.get(PropertyLabel.CODE.label).toString()
                group.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
                group.observation = item.get(PropertyLabel.OBSERVATION.label).toString()
                group.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
            }
            return group
        }
    }

//    override fun createEdge(target: VertexInfo): JSONResponse {
//        return FAILResponse(data = "Impossible create a edge from this vertex")
//    }
}