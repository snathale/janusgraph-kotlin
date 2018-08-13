package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

interface IValidator {
    fun canInsertVertex(vertex: ICommon): Boolean
    fun canUpdateVertexProperty(properties: List<Property>): Boolean
    fun hasVertexTarget(target: VertexInfo): Vertex?
    fun hasVertex(source: VertexInfo): Vertex?
    fun isCorrectVertexTarget(target: VertexInfo): Boolean
    fun hasProperty(vertex: VertexInfo, property: Property): Boolean
}