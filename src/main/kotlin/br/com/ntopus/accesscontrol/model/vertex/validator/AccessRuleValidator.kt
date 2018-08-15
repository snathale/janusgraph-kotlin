package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.ConfiguredGraphFactory

class AccessRuleValidator: DefaultValidator() {

    override fun canInsertVertex(vertex: ICommon): Boolean {
        if (vertex.code.isEmpty()) {
            return false
        }
        return true
    }

    override fun hasVertexTarget(target: VertexInfo): Vertex? {
//        ConfiguredGraphFactory.create("")
        val g = graph.traversal()
        return try {
            when(target.label) {
                VertexLabel.ACCESS_GROUP.label -> g.V().hasLabel(VertexLabel.ACCESS_GROUP.label)
                        .has(PropertyLabel.CODE.label, target.code).next()
                VertexLabel.GROUP.label -> g.V().hasLabel(VertexLabel.GROUP.label)
                        .has(PropertyLabel.CODE.label, target.code).next()
                VertexLabel.UNIT_ORGANIZATION.label -> g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
                        .has(PropertyLabel.CODE.label, target.code).next()
                VertexLabel.ORGANIZATION.label -> g.V().hasLabel(VertexLabel.ORGANIZATION.label)
                        .has(PropertyLabel.CODE.label, target.code).next()
                else -> null
            }
        } catch (e: Exception) {
            return null
        }
    }

    override fun hasVertex(code: String): Vertex? {
        return try {
            val g = graph.traversal()
            g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, code).next()
        } catch (e: Exception) {
            null
        }
    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        val g = graph.traversal()
        return when(target.label) {
            VertexLabel.ACCESS_GROUP.label -> target.label == VertexLabel.ACCESS_GROUP.label
            VertexLabel.GROUP.label -> target.label == VertexLabel.GROUP.label
            VertexLabel.UNIT_ORGANIZATION.label -> target.label == VertexLabel.UNIT_ORGANIZATION.label
            VertexLabel.ORGANIZATION.label -> target.label == VertexLabel.ORGANIZATION.label
            else -> false
        }
    }

    override fun hasProperty(code: String, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(property.name, property.value) != null
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        for (value in properties) {
            if (value.name != PropertyLabel.NAME.label
                    && value.name != PropertyLabel.EXPIRATION_DATE.label
                    && value.name != PropertyLabel.ENABLE.label) return false
        }
        return true
    }
}