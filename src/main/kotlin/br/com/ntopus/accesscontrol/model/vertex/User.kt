package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import org.janusgraph.core.JanusGraph
import java.util.*

class User: CommonAgent(){
    override fun insert(graph: JanusGraph): Long {
        val user = graph.addVertex("user")
        user.property("name", this.name)
        user.property("code", this.code)
        user.property("observation", this.observation)
        user.property("creationDate", Date())
        user.property("enable", true)
        graph.tx().commit()
        return user.id() as Long
    }

//    @Adjacency(label= "associated")
//    abstract override fun add(vertex: Common)
}