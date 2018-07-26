package transversal

import data.EdgeData
import data.PropertyKey
import data.VertexData
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource
import org.janusgraph.core.JanusGraph
import kotlin.system.exitProcess

class Graph(var graph: JanusGraph, var g: GraphTraversalSource): IGraph {
    override fun addVertex(vertex: VertexData): Long {
        try {
            val v = graph.addVertex(vertex.label)
            for (propertyKey: PropertyKey in vertex.propertyKeys) {
                v.property(propertyKey.name, propertyKey.value)
            }
            return v.id() as Long
        } catch (e: Exception) {
            println ("Impossible add vertex ${e.message}")
            exitProcess(1)
        }
    }

    override fun addEdge(edge: EdgeData): Long {
        try {
            val vSource = g.V(edge.source)
            val vTarget = g.V(edge.target)
            val e = vSource.addE(edge.label).to(vTarget)
            return e.id() as Long
        } catch (e: Exception) {
            println ("Impossible add edge ${e.message}")
            exitProcess(1)
        }
    }
}