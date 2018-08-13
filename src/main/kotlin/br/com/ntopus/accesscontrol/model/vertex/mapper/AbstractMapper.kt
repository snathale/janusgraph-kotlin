package br.com.ntopus.accesscontrol.model.vertex.mapper

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.text.SimpleDateFormat
import java.util.*

object AbstractMapper {
    fun parseMapValue (value: String): String {
        if (value == "null") return ""
        return value.replace("[","").replace("]","")
    }

    fun parseMapVertex(vertex: GraphTraversal<Vertex, Vertex>): Map<String, String> {
        var values: Map<String, String> = mapOf()
        vertex.valueMap<String>().iterator().forEach {values+=it}
        return values
    }

    fun formatDate(date: String): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val defaultFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
        return format.format(defaultFormat.parse(AbstractMapper.parseMapValue(date)))
    }

    fun parseMapEdge(edge: GraphTraversal<Vertex, Edge>): Map<String, String> {
        var values: Map<String, String> = mapOf()
        edge.valueMap<String>().iterator().forEach {values+=it}
        return values
    }
}