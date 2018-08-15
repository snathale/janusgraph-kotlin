package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.AccessRule
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.AccessRuleValidator
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.text.SimpleDateFormat

class AccessRuleMapper (val properties: Map<String, String>): IMapper {
    private val accessRule = AccessRule(properties)
    private val graph = GraphFactory.open()

    override fun insert(): JSONResponse {
        if (!AccessRuleValidator().canInsertVertex(this.accessRule)) {
            return FAILResponse(data = "@ARCVE-001 Empty Access Rule properties")
        }
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE.label)
            accessRule.property(PropertyLabel.CODE.label, this.accessRule.code)
            accessRule.property(PropertyLabel.ENABLE.label, this.accessRule.enable)
            if (this.accessRule.expirationDate != null) {
                accessRule.property(PropertyLabel.EXPIRATION_DATE.label, this.accessRule.expirationDate)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCVE-002 ${e.message.toString()}")
        }
        val response = AssociationResponse(
                this.accessRule.code,
                this.accessRule.formatDate(),
                this.accessRule.enable)
        return SUCCESSResponse(data = response)
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        val accessRule = AccessRuleValidator().hasVertex(this.accessRule.code)
                ?: return FAILResponse(data = "@ARUPE-001 Impossible find Access Rule with code ${this.accessRule.code}")
        if (!AccessRuleValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@ARUPE-002 Access Rule property can be updated")
        }
        try {
            for (property in properties) {
                if (property.name == PropertyLabel.EXPIRATION_DATE.label) {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    accessRule.property(property.name, format.parse(property.value))
                    continue
                }
                accessRule.property(property.name, property.value)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARUPE-002 ${e.message.toString()}")
        }
        val traversal = graph.traversal().V().hasLabel(VertexLabel.ACCESS_RULE.label)
                .has(PropertyLabel.CODE.label, this.accessRule.code)
        val values = AbstractMapper.parseMapVertex(traversal)
        val response = AssociationResponse(
                this.accessRule.code,
                AbstractMapper.parseMapValueDate(values[PropertyLabel.EXPIRATION_DATE.label].toString())!!,
                AbstractMapper.parseMapValue(values[PropertyLabel.ENABLE.label].toString()).toBoolean()
        )
        return SUCCESSResponse(data = response)
    }

    override fun delete(): JSONResponse {
        val accessRule = AccessRuleValidator().hasVertex(this.accessRule.code)
                ?: return FAILResponse(data = "@ARDE-001 Impossible find Access Rule with code ${this.accessRule.code}")
        try {
            accessRule.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        if (!AccessRuleValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@ARCEE-001 Impossible create this edge with target code ${target.code}")
        }
        val vAccessGroup = AccessRuleValidator().hasVertex(this.accessRule.code)
                ?: return FAILResponse(data = "@ARCEE-002 Impossible find Access Rule with code ${this.accessRule.code}")
        val vTarget = AccessRuleValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@ARCEE-003 Impossible find ${target.label.capitalize()} with code ${target.code}")
        return when(target.label) {
            VertexLabel.ORGANIZATION.label,
            VertexLabel.UNIT_ORGANIZATION.label,
            VertexLabel.GROUP.label-> this.createProvideEdge(vAccessGroup, vTarget, target)
            VertexLabel.ACCESS_GROUP.label -> this.createOwnEdge(vAccessGroup, vTarget, target)
            else -> FAILResponse(data = "@ARCEE-006 Impossible create a edge from Access Rule with code ${this.accessRule.code}")
        }
    }

    private fun createProvideEdge(vSource: Vertex, vTarget: Vertex, target: VertexInfo): JSONResponse {
        try {
            vSource.addEdge(EdgeLabel.PROVIDE.label, vTarget)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCEE-004 ${e.message.toString()}")
        }
        val source = VertexInfo(VertexLabel.ACCESS_RULE.label, this.accessRule.code)
        return SUCCESSResponse(data = EdgeCreated(source, target, EdgeLabel.PROVIDE.label))
    }

    private fun createOwnEdge(vSource: Vertex, vTarget: Vertex, target: VertexInfo): JSONResponse {
        try {
            vSource.addEdge(EdgeLabel.OWN.label,vTarget)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCEE-005 ${e.message.toString()}")
        }
        val source = VertexInfo(VertexLabel.ACCESS_RULE.label, this.accessRule.code)
        return SUCCESSResponse(data = EdgeCreated(source, target, EdgeLabel.OWN.label))
    }
}