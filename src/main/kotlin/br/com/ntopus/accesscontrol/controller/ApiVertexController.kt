package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.factory.MapperFactory
import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.StatusResponse
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.ERRORResponse
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AbstractMapper
import com.google.gson.Gson
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.springframework.web.bind.annotation.*
import com.google.gson.GsonBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("api/v1/vertex")
class ApiVertexController {

    val graph = GraphFactory.open()
    @PostMapping("/add")
    fun addVertex(@RequestBody vertex: VertexData): ResponseEntity<Any> {
        var gson = Gson()
        try {
            gson = GsonBuilder().serializeNulls().create()
            val response = MapperFactory.createFactory(vertex).insert()
            if (response.status == StatusResponse.FAIL.toString()) {
                return ResponseEntity(gson.toJson(response), HttpStatus.NOT_FOUND)
            }
            return ResponseEntity(gson.toJson(response), HttpStatus.OK)
        } catch (e: Exception) {
            val response = ERRORResponse(message = "@ACIE-001 Impossible create a Vertex ${e.message}")
            return ResponseEntity(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/updateProperty/{label}/{code}")
    fun updateVertexProperty(@PathVariable label: String, @PathVariable code: String, @RequestBody properties: List<Property>): ResponseEntity<Any> {
        var gson = Gson()
        val vertex = VertexData(label, listOf(Property(PropertyLabel.CODE.label, code)))
        try {
            gson = GsonBuilder().serializeNulls().create()
            val response = MapperFactory.createFactory(vertex).updateProperty(properties)
            if (response.status == StatusResponse.FAIL.toString()) {
                return ResponseEntity(gson.toJson(response), HttpStatus.NOT_FOUND)
            }
            return ResponseEntity(gson.toJson(response), HttpStatus.OK)
        } catch (e: Exception) {
            val response = ERRORResponse(message = "@ACUPV-001 Impossible update Vertex Property ${e.message}")
            return ResponseEntity(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/delete/{code}")
    fun deleteVertex(@PathVariable code: String, @RequestBody vertex: String): ResponseEntity<Any> {
        var gson = Gson()
        val vertexData = VertexData(vertex, listOf(Property(PropertyLabel.CODE.label, code)))
        try {
            gson = GsonBuilder().serializeNulls().create()
            val response = MapperFactory.createFactory(vertexData).delete()
            if (response.status == StatusResponse.FAIL.toString()) {
                return ResponseEntity(gson.toJson(response), HttpStatus.NOT_FOUND)
            }
            return ResponseEntity(gson.toJson(response), HttpStatus.OK)
        } catch (e: Exception) {
            val response = ERRORResponse(message = "@ACDV-001 Impossible delete Vertex Property ${e.message}")
            return ResponseEntity(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/")
    fun get(@RequestParam(value = "id", defaultValue = "0") id: Long): ResponseEntity<Any> {
        val g = graph.traversal()
        val gson = GsonBuilder().serializeNulls().create()
        try {
            val vertex = g.V(id).next()
            return ResponseEntity(gson.toJson(SUCCESSResponse(data = AbstractMapper.parseMapVertexById(vertex))), HttpStatus.OK)
        }
        catch (e: Exception) {
            return ResponseEntity(gson.toJson(FAILResponse(data = "@ACGV-001 Vertex not found")), HttpStatus.NOT_FOUND)
        }
    }

    data class VertexList (val label: String, val properties: Map<String, Vertex>, val edgeIn: ArrayList<Edge>, val edgeOut: ArrayList<Edge>)
    data class Edge (val label: String, val properties: Map<String, Vertex>)
    @GetMapping("/list")
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