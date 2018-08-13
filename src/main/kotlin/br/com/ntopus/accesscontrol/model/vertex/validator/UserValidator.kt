package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class UserValidator : DefaultValidator() {

    override fun hasVertexTarget(target: VertexInfo): Vertex? {
        val graph = GraphFactory.open()
        return try {
            val g = graph.traversal()
            g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, target.code).next()
        } catch (e: Exception) {
            null
        }

    }

    override fun hasVertex(source: VertexInfo): Vertex? {
        val g = graph.traversal()
        return try {
            g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, source.code).next()
        }
        catch (e: Exception) {
            null
        }
    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        return target.label.equals(VertexLabel.ACCESS_RULE.label)
    }

    override fun hasProperty(vertex: VertexInfo, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.USER.label).has(property.name, property.value) != null
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