package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon
import br.com.ntopus.accesscontrol.model.vertex.base.IAgent
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo
import org.apache.tinkerpop.gremlin.structure.Vertex

abstract class DefaultValidator: IValidator {
    val graph = GraphFactory.open()
    override fun canInsertVertex(vertex: ICommon): Boolean {
        if ((vertex as IAgent).name.isEmpty() || vertex.code.isEmpty()) {
            return false
        }
        return true
    }

    override fun hasVertexTarget(target: VertexInfo): Vertex? {
        return null
    }

    override fun isCorrectVertexTarget(target: VertexInfo): Boolean {
        return false
    }

    override fun canUpdateVertexProperty(properties: List<Property>): Boolean {
        return false
    }
}