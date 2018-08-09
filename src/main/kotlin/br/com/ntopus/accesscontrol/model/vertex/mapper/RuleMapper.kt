package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.Rule
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.RuleValidator
import org.janusgraph.core.JanusGraph

class RuleMapper (val properties: Map<String, String>): IMapper {
    private val rule = Rule(properties)
    private val graph = GraphFactory.open()

    override fun createEdge(target: VertexInfo): JSONResponse {
        return FAILResponse(data = "@RCEE-001 Impossible create a edge from this vertex")
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!RuleValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@RUPE-001 Rule not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val user = g.V().hasLabel(VertexLabel.RULE.label)
            for (property in properties) {
                user.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@RUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.rule)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val rule = RuleValidator()
                .hasVertex(VertexInfo(VertexLabel.RULE.label, this.rule.code))
                ?: return FAILResponse(data = "@RDE-001 Impossible find Rule ${this.rule}")
        try {
            rule.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@RDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!RuleValidator().canInsertVertex(this.rule)) {
                throw Exception("@RCVE-001 Empty Rule properties")
            }
            val rule = graph.addVertex(VertexLabel.RULE.label)
            rule.property(PropertyLabel.NAME.label, this.rule.name)
            rule.property(PropertyLabel.CODE.label, this.rule.code)
            rule.property(PropertyLabel.CREATION_DATE.label, this.rule.creationDate)
            rule.property(PropertyLabel.DESCRIPTION.label, this.rule.description)
            rule.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@RCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.rule)
    }

}