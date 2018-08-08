package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.AccessRule
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.AccessRuleValidator

class AccessRuleMapper (val properties: Map<String, String>): IMapper {
    private val accessRule = AccessRule(properties)
    private val graph = GraphFactory.open()

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!AccessRuleValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@ARUPE-001 Access Rule not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val user = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
            for (property in properties) {
                user.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.accessRule)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val accessRule = AccessRuleValidator()
                .hasVertex(VertexInfo(VertexLabel.ACCESS_RULE.label, this.accessRule.code))
                ?: return FAILResponse(data = "@ARDE-001 Impossible find Access Rule ${this.accessRule}")
        try {
            accessRule.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }
    override fun insert(): JSONResponse {
        try {
            if (!AccessRuleValidator().canInsertVertex(this.accessRule)) {
                throw Exception("@ARCVE-001 Empty Access Rule properties")
            }
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE)
            accessRule.property(PropertyLabel.CODE.label, this.accessRule.code)
            accessRule.property(PropertyLabel.ENABLE.label, this.accessRule.enable)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, this.accessRule.expirationDate)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.accessRule)
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}