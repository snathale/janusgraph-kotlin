package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.AccessGroup
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.AccessGroupValidator
import jnr.constants.platform.Access
import org.janusgraph.core.JanusGraph

class AccessGroupMapper(val properties: Map<String, String>): IMapper {
    private val graph = GraphFactory.open()
    private val accessGroup = AccessGroup(properties)

    override fun updateProperty(properties: List<Property>): JSONResponse {
        return FAILResponse(data = "@AGUPE-001 Impossible update proprieties from this vertex")
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val user = AccessGroupValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.accessGroup.code))
                ?: return FAILResponse(data = "@AGDE-001 Impossible find Access Group ${this.accessGroup}")
        try {
            user.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!AccessGroupValidator().canInsertVertex(this.accessGroup)) {
                throw Exception("@AGCVE-001 Empty Access Group properties")
            }
            val accessGroup = graph.addVertex(VertexLabel.ACCESS_GROUP.label)
            accessGroup.property(PropertyLabel.NAME.label, this.accessGroup.name)
            accessGroup.property(PropertyLabel.CODE.label, this.accessGroup.code)
            accessGroup.property(PropertyLabel.CREATION_DATE.label, this.accessGroup.creationDate)
            accessGroup.property(PropertyLabel.ENABLE.label, this.accessGroup.enable)
            if (!this.accessGroup.description.isEmpty()) {
                accessGroup.property(PropertyLabel.DESCRIPTION.label, this.accessGroup.description)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.accessGroup)
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}