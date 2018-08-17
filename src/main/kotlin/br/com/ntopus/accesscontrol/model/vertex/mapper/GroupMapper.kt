package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.Group
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.GroupValidator
import br.com.ntopus.accesscontrol.model.vertex.validator.UserValidator

class GroupMapper (val properties: Map<String, String>): IMapper {
    private val group = Group(properties)
    private val graph = GraphFactory.open()

    override fun insert(): JSONResponse {
        if (!GroupValidator().canInsertVertex(this.group)) {
            return FAILResponse(data = "@GCVE-001 Empty Group properties")
        }
        try {
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
        val response = AgentResponse(
                this.group.code, this.group.name,
                this.group.formatDate(), this.group.enable, this.group.observation
        )
        return SUCCESSResponse(data = response)
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        val group = GroupValidator().hasVertex(this.group.code)
                ?: return FAILResponse(data = "@GUPE-001 Impossible find Group with code ${this.group.code}")

        if (!GroupValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@GUPE-002 Group property can be updated")
        }
        try {
            for (property in properties) {
                group.property(property.name, property.value)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GUPE-003 ${e.message.toString()}")
        }
        val traversal = graph.traversal().V().hasLabel(VertexLabel.GROUP.label).has(PropertyLabel.CODE.label, this.group.code)
        val values = AbstractMapper.parseMapVertex(traversal)
        val response = AgentResponse(
                this.group.code,
                AbstractMapper.parseMapValue(values[PropertyLabel.NAME.label].toString()),
                AbstractMapper.parseMapValueDate(values[PropertyLabel.CREATION_DATE.label].toString())!!,
                AbstractMapper.parseMapValue(values[PropertyLabel.ENABLE.label].toString()).toBoolean(),
                AbstractMapper.parseMapValue((values[PropertyLabel.OBSERVATION.label].toString()))
        )
        return SUCCESSResponse(data = response)
    }

    override fun delete(): JSONResponse {
        val group = GroupValidator().hasVertex(this.group.code)
                ?: return FAILResponse(data = "@GDE-001 Impossible find Group with code ${this.group.code}")
        try {
            group.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun createEdge(target: VertexInfo, edgeTarget: String): JSONResponse {
        if (!GroupValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@GCEE-001 Impossible create edge with target code ${target.code}")
        }
        val vSource = GroupValidator().hasVertex(this.group.code)
                ?: return FAILResponse(data = "@GCEE-002 Impossible find Group with code ${this.group.code}")

        val vTarget = GroupValidator().hasVertexTarget(target)
                ?: return FAILResponse(data ="@GCEE-003 Impossible find Group with code ${target.code}")
        try {
            vSource.addEdge(EdgeLabel.HAS.label, vTarget)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@GCEE-004 ${e.message.toString()}")
        }
        val response = EdgeCreated(VertexInfo(VertexLabel.GROUP.label, this.group.code), target, EdgeLabel.HAS.label)
        return SUCCESSResponse(data = response)
    }
}