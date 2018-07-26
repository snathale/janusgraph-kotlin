package transversal

import data.EdgeData
import data.PropertyKey
import data.VertexData
import org.janusgraph.core.JanusGraph
import kotlin.system.exitProcess
import org.apache.tinkerpop.gremlin.process.traversal.P.within
import org.apache.tinkerpop.gremlin.structure.Vertex


class Graph(var graph: JanusGraph): IGraph {
    override fun updateProperty(id: Long, property: PropertyKey) {
    }

    override fun addVertex(vertex: VertexData): Long {
        try {
            val v = graph.addVertex(vertex.label)
            for (propertyKey: PropertyKey in vertex.propertyKeys) {
                v.property(propertyKey.name, propertyKey.value)
            }
            graph.tx().commit()
            return v.id() as Long
        } catch (e: Exception) {
            graph.tx().rollback()
            println ("Impossible add vertex ${e.message}")
            exitProcess(1)
        }
    }

    override fun addEdge(edge: EdgeData): Boolean {
        val g = graph.traversal()
        try {
            val vSource = g.V(edge.source)
            val vTarget = g.V(edge.target)
            val e = vSource.addE(edge.label).to(vTarget).next()
            graph.tx().commit()
            return true
        } catch (e: Exception) {
            g.tx().rollback()
            println ("Impossible add edge ${e.message}")
            exitProcess(1)
        }
    }

    override fun listVertex(limit: Long): MutableList<Vertex> {
        return try {
            val g = graph.traversal()
            g.V().limit(limit).toList()
        } catch (e: Exception) {
            println("Impossible list all Vertex ${e.message}")
            mutableListOf()
        }
    }

    override fun listEdges(limit: Long): MutableList<String> {
        return try {
            val g = graph.traversal()
            val nodes = g.V().limit(limit).toList()
            g.V(nodes).aggregate("node").outE().`as`("edge").inV().where(within("node"))
                    .select<String>("edge").toList()
        } catch (e: Exception) {
            println("Impossible list all Edge ${e.message}")
            mutableListOf()
        }
    }
}