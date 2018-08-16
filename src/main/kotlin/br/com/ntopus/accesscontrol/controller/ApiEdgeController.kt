package br.com.ntopus.accesscontrol.controller

import br.com.ntopus.accesscontrol.factory.MapperFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.vertex.base.ERRORResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class EdgeContext(val source: VertexInfo, val target: VertexInfo, val edgeLabel: String? = "")
@RestController
@RequestMapping("api/v1/edge")
class ApiEdgeController {

    @PostMapping("/add")
    fun addEdge(@RequestBody params: EdgeContext): String {
        var gson = Gson()
        val vertex = VertexData(params.source.label, listOf(Property(PropertyLabel.CODE.label, params.source.code)))
        return try {
            gson = GsonBuilder().serializeNulls().create()
            return  gson.toJson(MapperFactory.createFactory(vertex).createEdge(params.target, params.edgeLabel.toString()))
        } catch (e: Exception) {
            gson.toJson(ERRORResponse(message = "@ACIE-001 Impossible create a Edge ${e.message}"))
        }
    }
}