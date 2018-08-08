package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

interface IValidator {
    fun canInsertVertex(vertex: ICommon): Boolean
    fun canUpdateVertexProperty(properties: List<Property>): Boolean
    fun hasVertexTarget(target: VertexInfo): GraphTraversal<Vertex, Vertex>?
    fun hasVertex(source: VertexInfo): GraphTraversal<Vertex, Vertex>?
    fun isCorrectVertexTarget(target: VertexInfo): Boolean
    fun hasProperty(vertex: VertexInfo, property: Property): Boolean
}