package transversal

import data.EdgeData
import data.PropertyKey
import data.VertexData

interface IGraph {
    fun addVertex(vertex: VertexData): Long
    fun addEdge(edge: EdgeData): Boolean
    fun updateProperty(id: Long, property: PropertyKey, isVertex: Boolean = true): Boolean
    fun listVertex(limit: Long)
    fun listEdges(limit: Long)
    fun verifyUserHasPermission(userCode: String, permission: String): Boolean

}