package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.factory.MapperFactory
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.base.ERRORResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import com.google.gson.Gson
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.springframework.web.bind.annotation.*
import com.google.gson.GsonBuilder

data class EdgeContext(val source: VertexInfo, val target: VertexInfo, val edgeLabel: String? = "")
@RestController
@RequestMapping("api/v1")
class ApiController {

    val graph = GraphFactory.open()
    @PostMapping("/addVertex")
    fun addVertex(@RequestBody vertex: VertexData): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return try {
            return  gson.toJson(MapperFactory.createFactory(vertex).insert())
        } catch (e: Exception) {
            gson.toJson(ERRORResponse(message = "@ACIE-001 Impossible create a Vertex ${e.message}"))
        }
    }

    @PostMapping("/addEdge")
    fun addEdge(@RequestBody params: EdgeContext): String {
        var gson = Gson()
        val vertex = VertexData(params.source.label, listOf(Property(PropertyLabel.CODE.label, params.source.code)))
        return try {
            gson = GsonBuilder().serializeNulls().create()
            return  gson.toJson(MapperFactory.createFactory(vertex).createEdge(params.target, params.edgeLabel))
        } catch (e: Exception) {
            gson.toJson(ERRORResponse(message = "@ACIE-001 Impossible create a Edge ${e.message}"))
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