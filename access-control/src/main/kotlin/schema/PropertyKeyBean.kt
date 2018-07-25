package schema

import org.janusgraph.core.Cardinality
import org.janusgraph.core.schema.JanusGraphManagement

class PropertyKeyBean {
    val name: String? = null
    val dataType: String = ""
    val cardinality: String = ""

    fun make(mgmt: JanusGraphManagement) {
        if (this.name == null) {
            println("need \"name\" property to define a propertyKey")
        } else if (mgmt.containsPropertyKey(name)) {
            println("property: ${name} exists")
        } else {
            try {
                mgmt.makePropertyKey(name)
                        .dataType(TypeMap.MAP.get(dataType))
                        .cardinality(Cardinality.valueOf(this.cardinality)).make()
                println("propertyKey:${name} creation is done")
            } catch (e: Exception) {
                println("can't create property:${name}, ${e.message}")
            }
        }
    }
}