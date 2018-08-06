package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.StatusResponse
import br.com.ntopus.accesscontrol.model.data.EdgeData
import br.com.ntopus.accesscontrol.model.vertex.*
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.edge.Has
import br.com.ntopus.accesscontrol.model.vertex.base.ERRORResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.janusgraph.core.JanusGraph
import org.springframework.web.bind.annotation.*
import com.google.gson.GsonBuilder


@RestController
@RequestMapping("api/v1")
class ApiController() {

    val graph = GraphFactory.open()
    @PostMapping("/addVertex")
    fun addVertex(@RequestBody vertex: VertexData): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val propertiesMap= vertex.properties.associateBy({it.name}, {it.value})
        when(vertex.label) {
            "user" -> return gson.toJson(User(propertiesMap).insert())
            "organization" -> return gson.toJson(Organization(propertiesMap).insert())
            "unitOrganization" -> gson.toJson(UnitOrganization(propertiesMap).insert())
            "group" -> return gson.toJson(Group(propertiesMap).insert())
            "rule" -> return gson.toJson(Rule(propertiesMap).insert())
            "accessGroup" -> return gson.toJson(AccessGroup(propertiesMap).insert())
            "accessRule" -> return gson.toJson(AccessRule(propertiesMap).insert())
            else -> return gson.toJson(ERRORResponse(message = "Impossible createEdge vertex with this label"))
        }
        return gson.toJson(ERRORResponse(message = "Impossible createEdge this vertex"))
    }

    @PostMapping("/addEdge")
    fun addEdge(@RequestBody edge: EdgeData) {

        when(edge.source.label) {
//            "user" ->
//            "provide"
//            "own"
//            "createEdge"
//            "remove"
//            "associated"
//            "inherit"
        }
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