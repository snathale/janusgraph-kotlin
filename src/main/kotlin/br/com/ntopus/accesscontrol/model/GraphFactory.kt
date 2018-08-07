package br.com.ntopus.accesscontrol.model

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.core.io.ClassPathResource

object GraphFactory {
    private val graph: JanusGraph
    init {
        graph = JanusGraphFactory.open(ClassPathResource("janusgraph-cql-es.properties").file.absolutePath)
    }
    fun open() = graph
}