package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.factory.MapperFactory
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeData
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.base.ERRORResponse
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.springframework.web.bind.annotation.*
import com.google.gson.GsonBuilder


@RestController
@RequestMapping("api/v1")
class ApiController {

    val graph = GraphFactory.open()
    @PostMapping("/addVertex")
    fun addVertex(@RequestBody vertex: VertexData): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return try {
            val t = MapperFactory.createFactory(vertex).insert()
            gson.toJson(t)
        } catch (e: Exception) {
            gson.toJson(ERRORResponse(message = "Impossible create a Vertex with this label"))
        }
    }

    @PostMapping("/addEdge")
    fun addEdge(@RequestBody edge: EdgeData): String {
//        val vertex = VertexInfo(edge.target.label, edge.target.code)
//        val gson = GsonBuilder().setPrettyPrinting().create()
//        return when(edge.source.label) {
//            "user" -> gson.toJson(User.findByCode(edge.source.code).createEdge(vertex))
//            "organization" -> gson.toJson(Organization.findByCode(edge.source.code).createEdge(vertex))
//            "unitOrganization" -> gson.toJson(UnitOrganization.findByCode(edge.source.code).createEdge(vertex))
//            "group" -> gson.toJson(Group.findByCode(edge.source.code).createEdge(vertex))
//            "rule" -> gson.toJson(Rule.findByCode(edge.source.code).createEdge(vertex))
//            "accessGroup" -> gson.toJson(AccessGroup.findByCode(edge.source.code).createEdge(vertex))
//            "accessRule" -> gson.toJson(AccessRule.findByCode(edge.source.code).createEdge(vertex))
//            else -> gson.toJson(ERRORResponse(message = "Impossible create a Edge this label"))
//        }
        return ""
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