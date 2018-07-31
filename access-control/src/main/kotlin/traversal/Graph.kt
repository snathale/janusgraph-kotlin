package traversal

import data.EdgeData
import data.PropertyKey
import data.VertexData
import org.janusgraph.core.JanusGraph
import kotlin.system.exitProcess
import org.apache.tinkerpop.gremlin.process.traversal.P.within
import org.apache.tinkerpop.gremlin.structure.Vertex


class Graph(var graph: JanusGraph): IGraph {
    override fun updateProperty(id: Long, property: PropertyKey, isVertex: Boolean): Boolean {
        try {
            val g = graph.traversal()
            if (!isVertex) {
                g.E(id).has(property.name).property(property.name, property.value).next()
                graph.tx().commit()
                println("[TRAVERSAL] Update property ${property.name} with value ${property.value} in Edge id $id")
                return true
            }
            g.V(id).property(property.name, property.value).next()
            graph.tx().commit()
            println("[TRAVERSAL] Update property ${property.name} with value ${property.value} in Vertex id $id")
            return true
        } catch (e: Exception) {
            graph.tx().rollback()
            graph.close()
            println ("[TRAVERSAL] Impossible update property vertex ${e.message}")
            exitProcess(1)
        }
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
            graph.close()
            println ("[TRAVERSAL] Impossible add vertex ${e.message}")
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
            g.close()
            println ("[TRAVERSAL] Impossible add edge ${e.message}")
            exitProcess(1)
        }
    }

    override fun listVertex(limit: Long)  {
        return try {
            val g = graph.traversal()
            val list = g.V().limit(limit).toList()
            for (item in list) {
                val property = g.V(item.id()).valueMap<Vertex>()
                for (content in property) {
                    println("[TRAVERSAL] Vertex id: ${item.id()}}, properties: ${content}")
                }
            }
        } catch (e: Exception) {
            println("[TRAVERSAL] Impossible list all Vertex ${e.message}")
        }
    }

    override fun listEdges(limit: Long) {
        return try {
            val g = graph.traversal()
            val nodes = g.V().limit(limit).toList()
            println("[TRAVERSAL] Edges: [id][target->property->source]")
            println ("[TRAVERSAL] ${g.V(nodes).aggregate("node").outE().`as`("edge").inV().where(within("node"))
                    .select<String>("edge")} ")

        } catch (e: Exception) {
            println("[TRAVERSAL] Impossible list all Edge ${e.message}")
        }
    }

    override fun verifyUserHasPermission(userCode: String, permission: String): Boolean {
        try {
            val g = graph.traversal()
            val user = g.V().has("code", userCode).outE("associated")
            if (user != user){
                if (user.outE("own").outE("add").property("name", permission) != null)
                    println("[TRAVERSAL] User have this access rule")
                    return true
            }
            println("[TRAVERSAL] User $userCode don't have access rule associated")
            return false
        } catch (e: Exception) {
            println("[TRAVERSAL] Impossible find permission ${e.message}")
            return false
        }
    }
}