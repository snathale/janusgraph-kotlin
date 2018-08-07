package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.Group
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.GroupValidator
import org.janusgraph.core.JanusGraph

class GroupMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    private val group = Group(properties)
    override fun insert(): JSONResponse {
        try {
            if (!GroupValidator().beforeInsert(this.group)) {
                throw Exception("Empty Group properties")
            }
            val group = graph.addVertex(VertexLabel.GROUP.label)
            group.property(PropertyLabel.NAME.label, this.group.name)
            group.property(PropertyLabel.CODE.label, this.group.code)
            group.property(PropertyLabel.OBSERVATION.label, this.group.observation)
            group.property(PropertyLabel.CREATION_DATE.label, this.group.creationDate)
            group.property(PropertyLabel.ENABLE.label, this.group.enable)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun updateProperty(vertex: VertexInfo, property: Property): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(vertex: VertexInfo, code: String): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}