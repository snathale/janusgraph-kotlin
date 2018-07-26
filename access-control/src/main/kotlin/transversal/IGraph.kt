package transversal

import data.EdgeData
import data.VertexData

interface IGraph {
    fun addVertex(vertex: VertexData): Long
    fun addEdge(edge: EdgeData): Long
}