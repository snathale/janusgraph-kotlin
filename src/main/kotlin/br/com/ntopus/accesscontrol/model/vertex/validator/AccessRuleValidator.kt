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
    override fun hasVertex(source: VertexInfo): Vertex? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canInsertVertex(vertex: ICommon): Boolean {
        if (vertex.code.isEmpty()) {
            return false
        }
        return true
    }

//    override fun hasVertexTarget(target: VertexInfo): GraphTraversal<Vertex, Vertex>? {
//        ConfiguredGraphFactory.create("")
//        val g = graph.traversal()
//        return when(target.label) {
//            VertexLabel.ACCESS_GROUP.label -> g.V().hasLabel(VertexLabel.ACCESS_GROUP.label)
//                    .has(PropertyLabel.CODE.label, target.code)
//            VertexLabel.GROUP.label -> g.V().hasLabel(VertexLabel.GROUP.label)
//                    .has(PropertyLabel.CODE.label, target.code)
//            VertexLabel.UNIT_ORGANIZATION.label -> g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
//                    .has(PropertyLabel.CODE.label, target.code)
//            VertexLabel.ORGANIZATION.label -> g.V().hasLabel(VertexLabel.ORGANIZATION.label)
//                    .has(PropertyLabel.CODE.label, target.code)
//            else -> null
//        }
//    }

//    override fun hasVertex(source: VertexInfo): GraphTraversal<Vertex, Vertex>? {
//        val g = graph.traversal()
//        return g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, source.code)
//    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        val g = graph.traversal()
        return when(target.label) {
            VertexLabel.ACCESS_GROUP.label -> target.label.equals(VertexLabel.ACCESS_GROUP.label)
            VertexLabel.GROUP.label -> target.label.equals(VertexLabel.GROUP.label)
            VertexLabel.UNIT_ORGANIZATION.label -> target.label.equals(VertexLabel.UNIT_ORGANIZATION.label)
            VertexLabel.ORGANIZATION.label -> target.label.equals(VertexLabel.ORGANIZATION.label)
            else -> false
        }
    }

    override fun hasProperty(vertex: VertexInfo, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(property.name, property.value) != null
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        val iterator = properties.iterator()
        while (iterator.hasNext()) {
            when((iterator as Property).name) {
                PropertyLabel.NAME.label -> iterator.next()
                PropertyLabel.ENABLE.label -> iterator.next()
                else -> return false
            }
        }
        return true
    }
}