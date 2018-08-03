package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import org.janusgraph.core.JanusGraph
import java.util.*

class Organization: CommonAgent() {
    override fun insert(graph: JanusGraph): Long {
        val organization = graph.addVertex("organization")
        organization.property("name", this.name)
        organization.property("code", this.code)
        organization.property("observation", this.observation)
        organization.property("creationDate", Date())
        organization.property("enable", true)
        graph.tx().commit()
        return organization.id() as Long
    }

//    @Incidence(label = "has")
//    abstract override fun add(vertex: Common)
}