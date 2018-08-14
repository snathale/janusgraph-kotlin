package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class RuleValidator: DefaultValidator() {
    override fun hasProperty(code: String, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.RULE.label).has(PropertyLabel.CODE.label, code)
                .has(property.name, property.value).next() != null
    }

    override fun hasVertex(code: String): Vertex? {
        val g = graph.traversal()
        return try {
            g.V().hasLabel(VertexLabel.RULE.label).has(PropertyLabel.CODE.label, code).next()
        }
        catch (e: Exception) {
            null
        }
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        for (value in properties) {
            if (value.name != PropertyLabel.NAME.label
                    && value.name != PropertyLabel.DESCRIPTION.label
                    && value.name != PropertyLabel.ENABLE.label) return false
        }
        return true
    }
}