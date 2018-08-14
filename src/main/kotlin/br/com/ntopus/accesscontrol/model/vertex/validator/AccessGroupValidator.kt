package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class AccessGroupValidator: DefaultValidator() {

    override fun hasVertexTarget(target: VertexInfo): Vertex? {
        val g = graph.traversal()
        return when(target.label) {
            VertexLabel.ACCESS_GROUP.label -> g.V().hasLabel(VertexLabel.ACCESS_GROUP.label)
                    .has(PropertyLabel.CODE.label, target.code).next()
            VertexLabel.RULE.label -> g.V().hasLabel(VertexLabel.RULE.label)
                    .has(PropertyLabel.CODE.label, target.code).next()
            else -> null
        }
    }

    override fun hasVertex(code: String): Vertex? {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.ACCESS_GROUP.label).has(PropertyLabel.CODE.label, code).next()
    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        val g = graph.traversal()
        return when(target.label) {
            VertexLabel.ACCESS_GROUP.label -> target.label.equals(VertexLabel.ACCESS_GROUP.label)
            VertexLabel.RULE.label -> target.label.equals(VertexLabel.RULE.label)
            else -> false
        }
    }

    override fun hasProperty(code: String, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.ACCESS_GROUP.label).has(property.name, property.value) != null
    }

}