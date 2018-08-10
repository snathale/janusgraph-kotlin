package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class UserValidator: DefaultValidator() {

    override fun hasVertexTarget(target: VertexInfo): GraphTraversal<Vertex, Vertex>? {
        val graph = GraphFactory.open()
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, target.code)
    }

    override fun hasVertex(source: VertexInfo): GraphTraversal<Vertex, Vertex>? {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, source.code)
    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        return target.label.equals(VertexLabel.ACCESS_RULE.label)
    }

    override fun hasProperty(vertex: VertexInfo, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.USER.label).has(property.name, property.value) != null
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        val iterator = properties.iterator()
        while (iterator.hasNext()) {
            when((iterator as Property).name) {
                "name" -> iterator.next()
                "observation" -> iterator.next()
                "enable" -> iterator.next()
                else -> return false
            }
        }
        return true
    }
}