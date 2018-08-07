package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.util.*

class Rule(properties: Map<String, String>): IPermission(properties) {

//    override fun createEdge(target: VertexInfo): JSONResponse {
//        return FAILResponse(data = "Impossible create a edge from this vertex")
//    }

    companion object {
        fun findByCode(code: String): ICommon {
            val g = GraphFactory.open().traversal()
            val values = g.V().hasLabel(VertexLabel.RULE.label)
                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
            val rule = Rule(hashMapOf())
            for (item in values) {
                rule.name = item.get(PropertyLabel.NAME.label).toString()
                rule.code = item.get(PropertyLabel.CODE.label).toString()
                rule.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
                rule.description = item.get(PropertyLabel.DESCRIPTION.label).toString()
                rule.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
            }
            return rule
        }
    }

//    @Incidence(label = "has")
//    abstract override fun createEdge(vertex: ICommon)
}