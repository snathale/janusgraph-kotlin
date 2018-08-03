package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.factory.UserFactory
import br.com.ntopus.accesscontrol.model.vertex.*
import br.com.ntopus.accesscontrol.model.vertex.base.Permission
import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import com.syncleus.ferma.DelegatingFramedGraph
import br.com.ntopus.accesscontrol.model.data.VertexData
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.springframework.web.bind.annotation.*



@RestController
@RequestMapping("api/v1")
class ApiController(var graph: JanusGraph) {

    @PutMapping
    fun addVertex(@RequestBody vertex: VertexData) {
        val fg = DelegatingFramedGraph<JanusGraph>(
                graph,
                true,
                setOf(
                        CommonAgent::class.java,
                        Permission::class.java,
                        AccessGroup::class.java,
                        AccessRule::class.java,
                        Group::class.java,
                        Organization::class.java,
                        Rule::class.java,
                        UnitOrganization::class.java,
                        User::class.java)
        )
        val vertexFactory = UserFactory.createFactory(vertex.label)
        val v = fg.addFramedVertex(vertexFactory)
    }

    data class VertexList (val label: String, val properties: Map<String, Vertex>, val edgeIn: ArrayList<Edge>, val edgeOut: ArrayList<Edge>)
    data class Edge (val label: String, val properties: Map<String, Vertex>)
    @GetMapping("/listVertex")
    fun listVertex(@RequestParam(value = "limit", defaultValue = "10") limit: Long): ArrayList<VertexList> {
        val g = graph.traversal()
        val search = g.V().limit(limit).toList().iterator()
        val list: ArrayList<VertexList> = ArrayList()
        for (item in search) {
            var properties: Map<String, Vertex> = mapOf()
            g.V(item.id()).valueMap<Vertex>().iterator().forEach {properties+=it}
            val edgeOut = g.V(item.id()).outE().valueMap<Vertex>().iterator()
            val edgeOutLabel = g.V(item.id()).outE().label().iterator()
            val edgeIn = g.V(item.id()).inE().valueMap<Vertex>().iterator()
            val edgeInLabel = g.V(item.id()).inE().label().iterator()
            val listOut: ArrayList<Edge> = ArrayList()
            while (edgeOut.hasNext() && edgeOutLabel.hasNext()) {
                val propertiesOut = edgeOut.next()
                val label = edgeOutLabel.next()
                listOut+=Edge(label, propertiesOut)
            }
            val listIn: ArrayList<Edge> = ArrayList()
            while (edgeIn.hasNext() && edgeInLabel.hasNext()) {
                val propertiesIn = edgeIn.next()
                val label = edgeInLabel.next()
                listIn+=Edge(label, propertiesIn)
            }
            list+=VertexList(item.label(), properties, listIn, listOut)
        }
        return list
    }
}