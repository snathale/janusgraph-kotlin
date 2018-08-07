package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.UnitOrganization
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.UnitOrganizationValidator
import org.janusgraph.core.JanusGraph
import java.util.*

class UnitOrganizationMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    private val unitOrganization = UnitOrganization(properties)
    override fun insert(): JSONResponse {
        try {
            if (!UnitOrganizationValidator().beforeInsert(this.unitOrganization)) {
                throw Exception("Empty UnitOrganization properties")
            }
            val unitOrganization = graph.addVertex(VertexLabel.UNIT_ORGANIZATION.label)
            unitOrganization.property(PropertyLabel.NAME.label, this.unitOrganization.name)
            unitOrganization.property(PropertyLabel.CODE.label, this.unitOrganization.code)
            unitOrganization.property(PropertyLabel.OBSERVATION.label, this.unitOrganization.observation)
            unitOrganization.property(PropertyLabel.CREATION_DATE.label, Date())
            unitOrganization.property(PropertyLabel.ENABLE.label, true)
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