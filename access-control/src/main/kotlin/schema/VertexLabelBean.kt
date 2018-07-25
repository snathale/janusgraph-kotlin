package schema

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.VertexLabelMaker

class VertexLabelBean {
    val name: String? = null
    val partition: Boolean = false
    val useStatic: Boolean = false

    fun make(mgmt: JanusGraphManagement) {
        if (name == null) {
            println("need \"name\" property to define a vertex")
        } else if (mgmt.containsVertexLabel(name)) {
            println("vertex: ${name} exists")
        } else {
            try {
                val maker: VertexLabelMaker = mgmt.makeVertexLabel(name)
                if (partition) maker.partition()
                if (useStatic) maker.setStatic()
                maker.make()
                println ("vertex:${name} creation is done")
            } catch (e: Exception) {
                println("can't create vertex: ${name}, ${e.message}")
            }
        }
    }
}