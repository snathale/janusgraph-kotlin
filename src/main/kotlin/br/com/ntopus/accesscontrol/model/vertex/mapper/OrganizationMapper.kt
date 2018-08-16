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

    override fun insert(): JSONResponse {
        try {
            if (!OrganizationValidator().canInsertVertex(this.organization)) {
                return FAILResponse(data = "@OCVE-001 Empty Organization properties")
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
        val response = AgentResponse(
                this.organization.code, this.organization.name,
                this.organization.formatDate(), this.organization.enable, this.organization.observation
        )
        return SUCCESSResponse(data = response)
    }

    override fun createEdge(target: VertexInfo, edgeTarget: String): JSONResponse {
        if (!OrganizationValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@OCEE-001 Impossible create this edge with target code ${target.code}")
        }
        val organization = OrganizationValidator()
                .hasVertex(this.organization.code)
                ?: return FAILResponse(data = "@OCEE-002 Impossible find Organization with code ${this.organization.code}")

        val unitOrganization = OrganizationValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@OCEE-003 Impossible find Unit Organization with code ${target.code}")

        try {
            organization.addEdge(EdgeLabel.HAS.label, unitOrganization)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@OCEE-004 ${e.message.toString()}")
        }
        val response = EdgeCreated(VertexInfo(VertexLabel.ORGANIZATION.label, this.organization.code), target, EdgeLabel.HAS.label)
        return SUCCESSResponse(data = response)
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        val organization = OrganizationValidator()
                .hasVertex(this.organization.code)
                ?: return FAILResponse(data = "@OUPE-001 Impossible find Organization with code ${this.organization.code}")

        if (!OrganizationValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@OUPE-002 Organization property can be updated")
        }
        try {
            for (property in properties) {
                organization.property(property.name, property.value)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@OUPE-003 ${e.message.toString()}")
        }
        val traversal = graph.traversal().V().hasLabel(VertexLabel.ORGANIZATION.label)
                .has(PropertyLabel.CODE.label, this.organization.code)
        val values = AbstractMapper.parseMapVertex(traversal)
        val response = AgentResponse(
                this.organization.code,
                AbstractMapper.parseMapValue(values[PropertyLabel.NAME.label].toString()),
                AbstractMapper.parseMapValueDate(values[PropertyLabel.CREATION_DATE.label].toString())!!,
                AbstractMapper.parseMapValue(values[PropertyLabel.ENABLE.label].toString()).toBoolean(),
                AbstractMapper.parseMapValue((values[PropertyLabel.OBSERVATION.label].toString()))
        )
        return SUCCESSResponse(data = response)
    }

    override fun delete(): JSONResponse {
        val organization = OrganizationValidator()
                .hasVertex(this.organization.code)
                ?: return FAILResponse(data = "@ODE-001 Impossible find Organization with code ${this.organization.code}")
        try {
            organization.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ODE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }
}