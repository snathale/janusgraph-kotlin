package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class GroupValidator : DefaultValidator() {
    override fun hasVertex(code: String): Vertex? {
        return try {
            graph.traversal().V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, code).next()
        } catch (e: Exception) {
            null
        }
    }

    override fun hasProperty(code: String, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, code)
                .has(property.name, property.value) != null
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        for (value in properties) {
            if (value.name != PropertyLabel.NAME.label
                    && value.name != PropertyLabel.OBSERVATION.label
                    && value.name != PropertyLabel.ENABLE.label) return false
        }
        return true
    }
}