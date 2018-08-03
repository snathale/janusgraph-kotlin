package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import org.janusgraph.core.JanusGraph
import java.util.*


class Group: CommonAgent() {
    override fun insert(graph: JanusGraph): Long {
        val group = graph.addVertex("group")
        group.property("name", this.name)
        group.property("code", this.code)
        group.property("observation", this.observation)
        group.property("creationDate", Date())
        group.property("enable", true)
        graph.tx().commit()
        return group.id() as Long
    }
}