package br.com.ntopus.accesscontrol.model.vertex.base

import org.janusgraph.core.JanusGraph

abstract class Common {

    val code: String = ""
    val enable: Boolean = true

    abstract fun insert (graph: JanusGraph): Long
}