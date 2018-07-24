package demo.schema

import org.apache.tinkerpop.gremlin.process.traversal.Order
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.JanusGraphManagement
import org.apache.tinkerpop.gremlin.structure.Direction

class VertexCentricIndexBean {
    private val name: String? = null
    private val edge: String? = null
    private val propertyKeys: MutableList<String>? = mutableListOf<String>()
    private val order: String = "incr"
    private val direction: String = "BOTH"

    fun make(mgmt: JanusGraphManagement) {
        if (name == null) {
            println ("missing 'name' property, not able to create a vertex-centric index")
            return
        }

        if (edge == null) {
            println ("vertex-centric index needs 'edge' property to specify a edge label")
            return
        }

        val elabel = mgmt.getEdgeLabel(edge)

        if (elabel == null) {
            println ("edge: ${edge} doesn't exist")
            return
        }

        if (mgmt.containsRelationIndex(elabel, name)) {
            println ("vertex-centric index: ${name} exists")
            return
        }

        if (propertyKeys == null || propertyKeys.size == 0) {
            println ("missing 'propertyKeys property, not able to create an index")
            return
        }

        val keys: Array<PropertyKey> = arrayOf<PropertyKey>()
        var counter: Int = 0
        for (property in propertyKeys) {
            val key = mgmt.getPropertyKey(property)
            if (key == null) {
                println ("propertyKey:${property} doesn't exist, can't create ${name} vertex-centric index")
                return
            }
            keys[counter++] = mgmt.getPropertyKey(property)
        }

        mgmt.buildEdgeIndex(elabel, name, Direction.valueOf(direction), Order.valueOf(order), keys as PropertyKey)

        println ("vertex-centric index: ${name} creation is done")
    }

}