package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class UnitOrganizationValidator: DefaultValidator() {
    override fun hasVertex(source: VertexInfo): Vertex? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun hasVertexTarget(target: VertexInfo): GraphTraversal<Vertex, Vertex>? {
//        val g = graph.traversal()
//        return g.V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, target.code)
//    }
//
//    override fun hasVertex(source: VertexInfo): GraphTraversal<Vertex, Vertex>? {
//        val g = graph.traversal()
//        return g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(PropertyLabel.CODE.label, source.code)
//    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        return target.label.equals(VertexLabel.GROUP.label)
    }

    override fun hasProperty(vertex: VertexInfo, property: Property): Boolean {
        val g = graph.traversal()
        return g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label).has(property.name, property.value) != null

    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        val iterator = properties.iterator()
        while (iterator.hasNext()) {
            when((iterator as Property).name) {
                PropertyLabel.NAME.label -> iterator.next()
                PropertyLabel.OBSERVATION.label -> iterator.next()
                PropertyLabel.ENABLE.label -> iterator.next()
                else -> return false
            }
        }
        return true
    }
}