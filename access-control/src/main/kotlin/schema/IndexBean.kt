package schema

import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.JanusGraphManagement.IndexBuilder
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.Edge
import org.janusgraph.core.schema.JanusGraphSchemaType

class IndexBean {
    val name: String? = null
    val propertyKeys: MutableList<String> = mutableListOf()
    val composite: Boolean = true
    val unique: Boolean = false
    val indexOnly: String? = null
    val mixedIndex: String? = null

    fun make(mgmt: JanusGraphManagement, isVertexIndex: Boolean) {
        if (name == null) {
            println ("missing the 'name' property, not able to create an index")
            return
        }

        if (mgmt.containsGraphIndex(name)) {
            println ("index: ${name} exists")
            return
        }

        if (propertyKeys.size == 0) {
            println ("missing the 'propertyKeys property, not able to create an index")
            return
        }
        val ib: IndexBuilder = mgmt.buildIndex(name,  if (isVertexIndex) Vertex::class.java else Edge::class.java )
        for (property in propertyKeys) {
            ib.addKey(mgmt.getPropertyKey(property))
        }

        if (isVertexIndex && unique) {
            ib.unique()
        }

        //indexOnly
        if (indexOnly != null) {
            var key: JanusGraphSchemaType? = null
            if (isVertexIndex) {
                key = mgmt.getVertexLabel(indexOnly)
            } else {
                key = mgmt.getEdgeLabel(indexOnly)
            }

            if (key == null) {
                println ("${indexOnly} doesn't exist, skip only property")
            } else {
                ib.indexOnly(key)
            }
        }

        if (composite) {
            ib.buildCompositeIndex()
        }

        if (mixedIndex != null) {
            ib.buildMixedIndex(mixedIndex)
        }

        println ("index: ${name} creation is done")
    }

}