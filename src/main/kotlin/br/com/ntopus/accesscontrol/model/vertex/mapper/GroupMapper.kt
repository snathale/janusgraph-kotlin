package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.Group
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.GroupValidator

class GroupMapper (val properties: Map<String, String>): IMapper {
    private val group = Group(properties)
    private val graph = GraphFactory.open()

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!GroupValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@GUPE-001 Group not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val group = g.V().hasLabel(VertexLabel.GROUP.label)
            for (property in properties) {
                group.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.group)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val group = GroupValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.group.code))
                ?: return FAILResponse(data = "@GDE-001 Impossible find Group ${this.group}")
        try {
            group.property(PropertyLabel.ENABLE, false).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!GroupValidator().canInsertVertex(this.group)) {
                throw Exception("@GCVE-001 Empty Group properties")
            }
            val group = graph.addVertex(VertexLabel.GROUP.label)
            group.property(PropertyLabel.NAME.label, this.group.name)
            group.property(PropertyLabel.CODE.label, this.group.code)
            if (!this.group.observation.isEmpty()) {
                group.property(PropertyLabel.OBSERVATION.label, this.group.observation)
            }
            group.property(PropertyLabel.CREATION_DATE.label, this.group.creationDate)
            group.property(PropertyLabel.ENABLE.label, this.group.enable)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.group)
    }

    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
        return FAILResponse(data = "@GCEE-001 Impossible create a edge from this vertex")
    }
}