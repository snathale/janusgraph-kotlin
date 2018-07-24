package cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import kotlin.system.exitProcess

interface GraphCommand {
    var label: String
    var property: MutableMap<String, String>
}

data class Vertex(override var label: String = "", override var property: MutableMap<String, String> = HashMap()): GraphCommand
data class Edge(override var label: String = "", override var property: MutableMap<String, String> = HashMap(), var source: Int = 0, var target: Int = 0): GraphCommand
class Cli : CliktCommand() {

    val vertex: String by option(help = "Choose a vertexLabel")
            .choice("organization", "unitOrganization", "group", "user", "accessGroup", "rule", "accessRule")
            .default("organization")
    val edge: String? by option(help = "Choose a edgeLabel")
            .choice("has", "provide", "own", "add", "remove", "associated", "inherit")
            .default("has")
    override fun run() {
        val vertexOption = Vertex(vertex)
        context.obj = vertexOption
        if (edge != null) {
            val edgeOption = Edge(edge.toString())
            context.obj = edgeOption
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
    val propertyLabels = arrayOf("name", "id", "code", "observation", "enable", "criationDate", "description", "expirationDate")
    override fun run() {
        for ((k, v) in property) {
            if (!propertyLabels.contains(k)) {
                TermUi.echo("Aborted! This property not is allowed for this graph", err = true)
                exitProcess(1)
            }
            graph.property[k] = v
        }
        if (graph is Edge && (source == null) || (target == null)) {
            TermUi.echo("Aborted! Edge needs a source and a target", err = true)
            exitProcess(1)
        }
        if (graph is Edge && (source != null) && (target != null) ) {
            TermUi.echo("Adding edge $graph")
            (graph as Edge).source = source!!.toInt()
            (graph as Edge).target = target!!.toInt()
            //call class to add Edge
        } else {
            TermUi.echo("Adding vertex $graph")
            //call class to add Vertex
        }
    }
}

fun main(args: Array<String>) = Cli()
      .subcommands(Add())
      .main(args)
