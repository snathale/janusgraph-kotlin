package demo.schema

import org.janusgraph.core.Multiplicity
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.schema.EdgeLabelMaker
import org.janusgraph.core.schema.JanusGraphManagement

class EdgeLabelBean {
    private val name:String? = null
    private val multiplicity: String = "MULTI"
    private val signatures: MutableList<String> = mutableListOf<String>()
    private val unidirected: Boolean = false

    fun make(mgmt: JanusGraphManagement) {
        if (name == null) {
            println ("need \"name\" property to define a label")
        } else if (mgmt.containsEdgeLabel(this.name)) {
            println("edge: ${name} exists")
        } else {
            try {
                val maker: EdgeLabelMaker = mgmt.makeEdgeLabel(name).multiplicity(Multiplicity.valueOf(this.multiplicity))
                if (this.signatures.size > 0) {
                    for (key in signatures) {
                        val property: PropertyKey = mgmt.getPropertyKey(key)
                        maker.signature(property)
                    }
                }
                if (unidirected) {
                    maker.unidirected()
                }
                maker.make()
                println("edge: ${name} creation is done")
            } catch (e: Exception) {
                println("cant't create edge: ${name}, ${e.message}")
            }
        }
    }
}