package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class RuleValidator: DefaultValidator() {

    override fun hasVertex(source: VertexInfo): GraphTraversal<Vertex, Vertex>? {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.RULE.label).has(PropertyLabel.CODE.label, source.code)
    }

    override fun hasProperty(vertex: VertexInfo, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.RULE.label).has(property.name, property.value) != null
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        val iterator = properties.iterator()
        while (iterator.hasNext()) {
            when((iterator as Property).name) {
                PropertyLabel.NAME.label -> iterator.next()
                PropertyLabel.DESCRIPTION.label -> iterator.next()
                PropertyLabel.ENABLE.label -> iterator.next()
                else -> return false
            }
        }
        return true
    }
}