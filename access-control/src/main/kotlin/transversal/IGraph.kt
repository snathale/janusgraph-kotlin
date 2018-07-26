package transversal

import data.EdgeData
import data.PropertyKey
import data.VertexData
import org.apache.tinkerpop.gremlin.structure.Vertex

interface IGraph {
    fun addVertex(vertex: VertexData): Long
    fun addEdge(edge: EdgeData): Boolean
    fun updateProperty(id: Long, property: PropertyKey)
    fun listVertex(limit: Long): MutableList<Vertex>
    fun listEdges(limit: Long): MutableList<String>
}