package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
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

class UnitOrganizationMapper (val properties: Map<String, String>): IMapper {
    private val unitOrganization = UnitOrganization(properties)
    private val graph = GraphFactory.open()

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!UnitOrganizationValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@UOUPE-001 Unit Organization not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val user = g.V().hasLabel(VertexLabel.UNIT_ORGANIZATION.label)
            for (property in properties) {
                user.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UOUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.unitOrganization)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val unitOrganization = UnitOrganizationValidator()
                .hasVertex(VertexInfo(VertexLabel.UNIT_ORGANIZATION.label, this.unitOrganization.code))
                ?: return FAILResponse(data = "@UODE-001 Impossible find Unit Organization ${this.unitOrganization}")
        try {
            unitOrganization.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UODE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!UnitOrganizationValidator().canInsertVertex(this.unitOrganization)) {
                throw Exception("@UOCVE-001 Empty Unit Organization properties")
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
            return FAILResponse(data = "@UOCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.unitOrganization)
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        if (UnitOrganizationValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@UOCEE-001 Impossible create this edge $target from UnitOrganization")
        }

        val unitOrganization = UnitOrganizationValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.unitOrganization.code)) ?:
                return FAILResponse(data = "@UOCEE-002 Impossible find Unit Organization $this")

        val group = UnitOrganizationValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@UOCEE-003 Impossible find Group $target")
        try {
            unitOrganization.addE(EdgeLabel.HAS.label).to(group).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UOCEE-004 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.unitOrganization)
    }
}