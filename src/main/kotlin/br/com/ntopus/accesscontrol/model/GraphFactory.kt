package br.com.ntopus.accesscontrol.model

import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphFactory
import org.springframework.core.io.ClassPathResource

object GraphFactory {
    private var graph: JanusGraph = JanusGraphFactory.open(ClassPathResource("janusgraph-cql-es.properties").file.absolutePath)
    fun open(): JanusGraph {
        return this.graph
    }
    fun setInstance (config: String): GraphFactory {
        this.graph = JanusGraphFactory.open(ClassPathResource(config).file.absolutePath)
        return this
    }
}