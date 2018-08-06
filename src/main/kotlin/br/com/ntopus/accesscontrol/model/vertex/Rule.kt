package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.util.*

class Rule(properties: Map<String, String>): Permission(properties){
    override fun findByCode(code: String): Common {
        val g = graph.traversal()
        val values = g.V().hasLabel(VertexLabel.RULE.label)
                .has(PropertyLabel.CODE.label, this.code).valueMap<Vertex>()
        for (item in values) {
            this.name = item.get(PropertyLabel.NAME.label).toString()
            this.code = item.get(PropertyLabel.CODE.label).toString()
            this.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
            this.description = item.get(PropertyLabel.DESCRIPTION.label).toString()
            this.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
        }
        return this
    }

    override fun insert(): JSONResponse {
        try {
            val rule = graph.addVertex("rule")
            rule.property("name", this.name)
            rule.property("code", this.code)
            rule.property("creationDate", this.creationDate)
            rule.property("enable", this.enable)
            rule.property("description", this.description)
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