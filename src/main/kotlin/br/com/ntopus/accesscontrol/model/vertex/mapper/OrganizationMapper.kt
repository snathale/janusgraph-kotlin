package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.Organization
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.OrganizationValidator
import org.janusgraph.core.JanusGraph

class OrganizationMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    val organization = Organization(properties)
    override fun insert(): JSONResponse {
        try {
            if (!OrganizationValidator().beforeInsert(this.organization)) {
                throw Exception("Empty Organization properties")
            }
            val organization = graph.addVertex(VertexLabel.ORGANIZATION.label)
            organization.property(PropertyLabel.NAME.label, this.organization.name)
            organization.property(PropertyLabel.CODE.label, this.organization.code)
            organization.property(PropertyLabel.OBSERVATION.label, this.organization.observation)
            organization.property(PropertyLabel.CREATION_DATE.label, this.organization.creationDate)
            organization.property(PropertyLabel.ENABLE.label, this.organization.enable)
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