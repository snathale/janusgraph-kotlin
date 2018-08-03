package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import org.janusgraph.core.JanusGraph
import java.util.*

class UnitOrganization: CommonAgent() {
    override fun insert(graph: JanusGraph): Long {
        val unitOrganization = graph.addVertex("unitOrganization")
        unitOrganization.property("name", this.name)
        unitOrganization.property("code", this.code)
        unitOrganization.property("observation", this.observation)
        unitOrganization.property("creationDate", Date())
        unitOrganization.property("enable", true)
        graph.tx().commit()
        return unitOrganization.id() as Long
    }

//    @Incidence(label = "has")
//    abstract override fun add(vertex: Common)
}