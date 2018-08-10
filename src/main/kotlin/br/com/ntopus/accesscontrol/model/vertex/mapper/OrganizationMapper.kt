package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.Organization
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.OrganizationValidator

class OrganizationMapper (val properties: Map<String, String>): IMapper {
    private val organization = Organization(properties)
    private val graph = GraphFactory.open()

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!OrganizationValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@OUPE-001 Organization not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val organization = g.V().hasLabel(VertexLabel.ORGANIZATION.label)
            for (property in properties) {
                organization.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@OUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.organization)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val organization = OrganizationValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.organization.code))
                ?: return FAILResponse(data = "@ODE-001 Impossible find Organization ${this.organization}")
        try {
            organization.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ODE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!OrganizationValidator().canInsertVertex(this.organization)) {
                throw Exception("@OCVE-001 Empty Organization properties")
            }
            val organization = graph.addVertex(VertexLabel.ORGANIZATION.label)
            organization.property(PropertyLabel.NAME.label, this.organization.name)
            organization.property(PropertyLabel.CODE.label, this.organization.code)
            if (!this.organization.observation.isEmpty()) {
                organization.property(PropertyLabel.OBSERVATION.label, this.organization.observation)
            }
            organization.property(PropertyLabel.CREATION_DATE.label, this.organization.creationDate)
            organization.property(PropertyLabel.ENABLE.label, this.organization.enable)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@OCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.organization)
    }

    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
        if (!OrganizationValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@OCEE-001 Impossible create this edge $target from Organization")
        }
        val organization = OrganizationValidator()
                .hasVertex(VertexInfo(VertexLabel.ORGANIZATION.label, this.organization.code))
                ?: return FAILResponse(data = "@OCEE-002 Impossible find this Organization ${this.organization}")

        val unitOrganization = OrganizationValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@OCEE-003 Impossible find this Unit Organization $target")

        try {
            organization.addE(EdgeLabel.HAS.label).to(unitOrganization).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@OCEE-004 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.organization)
    }
}