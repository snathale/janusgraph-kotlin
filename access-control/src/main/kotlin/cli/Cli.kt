package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.long
import data.EdgeData
import data.PropertyKey
import data.VertexData
import importer.JanusGraphSchemaImporter
import org.janusgraph.core.JanusGraphFactory
import traversal.Graph
import validator.VertexPropertyKey
import java.nio.file.Paths
import kotlin.system.exitProcess

interface GraphCommand {
    var label: String
    var property: MutableMap<String, String>
}

data class Vertex(override var label: String = "", override var property: MutableMap<String, String> = HashMap()): GraphCommand
data class Edge(override var label: String = "", override var property: MutableMap<String, String> = HashMap(), var source: Long = 0, var target: Long = 0): GraphCommand
class Test : CliktCommand() {

    val vertex: String by option(help = "Choose a vertexLabel")
            .choice("organization", "unitOrganization", "group", "user", "accessGroup", "rule", "accessRule")
            .default("organization")
    val edge: String? by option(help = "Choose a edgeLabel")
            .choice("has", "provide", "own", "add", "remove", "associated", "inherit")
    override fun run() {
        val vertexOption = Vertex(vertex)
        context.obj = vertexOption
        if (edge != null) {
            val edgeOption = Edge(edge.toString())
            context.obj = edgeOption
        }

    }
}

class LoadSchema: CliktCommand () {
    val loadSchema: Boolean by option("--on", help = "Choose if needs loading a Graph Schema")
            .flag("--off", "-O", default = false)
    override fun run() {
        if (loadSchema) {
            val path = Paths.get("").toAbsolutePath().toString()
            val janusGraph = JanusGraphFactory.open("$path/config/janusgraph-cql-es.properties")
            JanusGraphSchemaImporter().writeGraphSONSchema(janusGraph, "$path/config/schema.json")
            janusGraph.close()
        }
    }
}

class Add: CliktCommand () {
    val graph: GraphCommand by requireObject()
    val source: String? by option(help = "Setting a vertex source")
    val target: String? by option(help = "Setting a vertex target")
    val property: List<Pair<String, String>> by option(help = "Setting a property key/value pair.")
            .pair()
            .multiple()
    val propertyLabels = arrayOf("name", "id", "code", "observation", "enable", "creationDate", "description", "expirationDate")
    override fun run() {
        for ((k, v) in property) {
            if (!propertyLabels.contains(k)) {
                TermUi.echo("[CLI] Aborted! This property not is allowed for this graph", err = true)
                exitProcess(1)
            }
            graph.property[k] = v
        }
        if (graph is Edge && (source == null || target == null)) {
            TermUi.echo("[CLI] Aborted! Edge needs a source and a target", err = true)
            exitProcess(1)
        }
        val path = Paths.get("").toAbsolutePath().toString()
        val janusGraph = JanusGraphFactory.open("$path/config/janusgraph-cql-es.properties")
        if (graph is Edge && (source != null) && (target != null) ) {
            (graph as Edge).source = source!!.toLong()
            (graph as Edge).target = target!!.toLong()
            TermUi.echo("[CLI] Adding edge")
            Graph(janusGraph).addEdge(EdgeData(graph.label, source!!.toLong(), target!!.toLong()))
            println("[CLI] Add edge successfully")
        } else {
            TermUi.echo("[CLI] Adding vertex $graph")
            val propertyList: ArrayList<PropertyKey> = arrayListOf()
            val vertexLabel = (graph as Vertex).label
            for (item in property) {
                val newValue = PropertyKey(item.first, item.second)
                if (VertexPropertyKey.isPropertyIsCorrect(vertexLabel, newValue)) {
                    propertyList.add(newValue)
                }
            }
            val v = Graph(janusGraph).addVertex(VertexData(vertexLabel, propertyList))
            println("[CLI] Add vertex $v")
            println("[CLI-ADD] $v")
        }
        janusGraph.close()
    }
}

class ToList(): CliktCommand () {
    val option: String by option(help = "Choose list Vertex or Edge")
            .choice("vertex", "edge")
            .default("vertex")
    val limit: Long? by option(help = "Set a limit").long().default(10)
    override fun run() {
        val path = Paths.get("").toAbsolutePath().toString()
        val janusGraph = JanusGraphFactory.open("$path/config/janusgraph-cql-es.properties")
        if (option == "edge") {
            Graph(janusGraph).listEdges(limit!!.toLong())
        }else {
            Graph(janusGraph).listVertex(limit!!.toLong())
        }
        janusGraph.close()

    }
}

class UpdateProperty(): CliktCommand() {
    val isVertexProperty: Boolean? by option(help = "Choose if property is to Vertex or Edge")
            .flag(default = true)
    val id: Long by option(help = "Set a id").long().default(0)
    val property: Pair<String, String> by option(help = "Setting a property key/value pair.")
            .pair().default("" to "")
    val propertyLabels = arrayOf("name", "id", "code", "observation", "enable", "creationDate", "description", "expirationDate")
    override fun run() {
        if (!propertyLabels.contains(property.first)) {
            TermUi.echo("[CLI] Aborted! This property not is allowed for this graph", err = true)
            exitProcess(1)
        }
        val path = Paths.get("").toAbsolutePath().toString()
        val janusGraph = JanusGraphFactory.open("$path/config/janusgraph-cql-es.properties")
        val propertyUpdate = PropertyKey(property.first, property.second)
        Graph(janusGraph).updateProperty(id,propertyUpdate, isVertexProperty!!)
        janusGraph.close()
    }
}


fun main(args: Array<String>) = Test()
      .subcommands(Add()).subcommands(ToList()).subcommands(UpdateProperty()).subcommands(LoadSchema())
      .main(args)

