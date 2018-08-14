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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

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
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, this.accessRule.expirationDate)
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
        if (!AccessRuleValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@ARUPE-001 Access Rule not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val accessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
            for (property in properties) {
                accessRule.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.accessRule)
    }

    override fun delete(): JSONResponse {
        val accessRule = AccessRuleValidator()
                .hasVertex(this.accessRule.code)
                ?: return FAILResponse(data = "@ARDE-001 Impossible find Access Rule ${this.accessRule}")
        try {
            accessRule.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
        if (!AccessRuleValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@ARCEE-001 Impossible create this edge $target from Access Rule")
        }
        val accessGroup = AccessRuleValidator()
                .hasVertex(this.accessRule.code)
                ?: return FAILResponse(data = "@ARCEE-002 Impossible find Access Rule ${this.accessRule}")
        val vTarget = AccessRuleValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@ARCEE-003 Impossible find ${target.label.capitalize()} $target")
        return when(edgeLabel) {
//            EdgeLabel.PROVIDE.label -> this.createProvideEdge(accessGroup, vTarget, target)
//            EdgeLabel.OWN.label -> this.createOwnEdgeFromAccess(accessGroup, vTarget, target)
            else -> FAILResponse(data = "@ARCEE-006 Impossible create a edge from ${this.accessRule}")
        }
    }

    private fun createProvideEdge(
            vSource: GraphTraversal<Vertex, Vertex>, vTarget: GraphTraversal<Vertex, Vertex>, target: VertexInfo
    ): JSONResponse {
        try {
            vSource.addE(EdgeLabel.PROVIDE.label).to(vTarget).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCEE-004 ${e.message.toString()}")
        }
        val vertexInfo = VertexInfo(VertexLabel.ACCESS_GROUP.label, this.accessRule.code)
        return SUCCESSResponse(data = EdgeCreated(vertexInfo, target, EdgeLabel.PROVIDE.label))
    }

    private fun createOwnEdgeFromAccess(
            vSource: GraphTraversal<Vertex, Vertex>, vTarget: GraphTraversal<Vertex, Vertex>, target: VertexInfo
    ): JSONResponse {
        try {
            vSource.addE(EdgeLabel.OWN.label).to(vTarget).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@ARCEE-005 ${e.message.toString()}")
        }
        val vertexInfo = VertexInfo(VertexLabel.ACCESS_GROUP.label, this.accessRule.code)
        return SUCCESSResponse(data = EdgeCreated(vertexInfo, target, EdgeLabel.OWN.label))
    }
}