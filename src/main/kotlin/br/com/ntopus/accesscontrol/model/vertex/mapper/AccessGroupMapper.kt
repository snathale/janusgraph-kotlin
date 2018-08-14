package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.AccessGroup
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.AccessGroupValidator
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal
import org.apache.tinkerpop.gremlin.structure.Vertex

class AccessGroupMapper(val properties: Map<String, String>) : IMapper {
    private val graph = GraphFactory.open()
    private val accessGroup = AccessGroup(properties)

    override fun updateProperty(properties: List<Property>): JSONResponse {
        return FAILResponse(data = "@AGUPE-001 Impossible update proprieties from this vertex")
    }

    override fun delete(): JSONResponse {
        val user = AccessGroupValidator()
                .hasVertex(this.accessGroup.code)
                ?: return FAILResponse(data = "@AGDE-001 Impossible find Access Group ${this.accessGroup}")
        try {
            user.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }

    override fun insert(): JSONResponse {
        try {
            if (!AccessGroupValidator().canInsertVertex(this.accessGroup)) {
                throw Exception("@AGCVE-001 Empty Access Group properties")
            }
            val accessGroup = graph.addVertex(VertexLabel.ACCESS_GROUP.label)
            accessGroup.property(PropertyLabel.NAME.label, this.accessGroup.name)
            accessGroup.property(PropertyLabel.CODE.label, this.accessGroup.code)
            accessGroup.property(PropertyLabel.CREATION_DATE.label, this.accessGroup.creationDate)
            accessGroup.property(PropertyLabel.ENABLE.label, this.accessGroup.enable)
            if (!this.accessGroup.description.isEmpty()) {
                accessGroup.property(PropertyLabel.DESCRIPTION.label, this.accessGroup.description)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.accessGroup)
    }

    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
        if (!AccessGroupValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@AGCEE-001 Impossible create this edge $target from Access Group")
        }
        val accessGroup = AccessGroupValidator()
                .hasVertex(this.accessGroup.code)
                ?: return FAILResponse(data = "@AGCEE-002 Impossible find Access Group ${this.accessGroup}")
        val vTarget = AccessGroupValidator().hasVertexTarget(target)
                ?: return FAILResponse(data = "@AGCEE-003 Impossible find ${target.label.capitalize()} $target")
        return when(edgeLabel) {
//            EdgeLabel.ADD.label -> this.createAddEdgeFromRule(accessGroup, vTarget, target)
//            EdgeLabel.REMOVE.label -> this.createRemoveEdgeFromRule(accessGroup, vTarget, target)
//            EdgeLabel.INHERIT.label -> this.createInheritEdgeFromAccessGroup(accessGroup, vTarget, target)
            else -> FAILResponse(data = "@AGCEE-006 Impossible create a edge from ${this.accessGroup}")
        }
    }

    private fun createAddEdgeFromRule(
            vSource: GraphTraversal<Vertex, Vertex>, vTarget: GraphTraversal<Vertex, Vertex>, target: VertexInfo
    ): JSONResponse {
        try {
            vSource.addE(EdgeLabel.ADD.label).to(vTarget).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCEE-003 ${e.message.toString()}")
        }
        val vertexInfo = VertexInfo(VertexLabel.ACCESS_GROUP.label, this.accessGroup.code)
        return SUCCESSResponse(data = EdgeCreated(target, vertexInfo, EdgeLabel.ADD.label))
    }

    private fun createRemoveEdgeFromRule(
            vSource: GraphTraversal<Vertex, Vertex>, vTarget: GraphTraversal<Vertex, Vertex>, target: VertexInfo
    ): JSONResponse {
        try {
            vSource.addE(EdgeLabel.REMOVE.label).to(vTarget).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCEE-004 ${e.message.toString()}")
        }
        val vertexInfo = VertexInfo(VertexLabel.ACCESS_GROUP.label, this.accessGroup.code)
        return SUCCESSResponse(data = EdgeCreated(vertexInfo, target, EdgeLabel.REMOVE.label))
    }

    private fun createInheritEdgeFromAccessGroup(
            vSource: GraphTraversal<Vertex, Vertex>, vTarget: GraphTraversal<Vertex, Vertex>, target: VertexInfo
    ): JSONResponse {
        try {
            vSource.addE(EdgeLabel.INHERIT.label).to(vTarget).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@AGCEE-005 ${e.message.toString()}")
        }
        val vertexInfo = VertexInfo(VertexLabel.ACCESS_GROUP.label, this.accessGroup.code)
        return SUCCESSResponse(data = EdgeCreated(vertexInfo, target, EdgeLabel.INHERIT.label))
    }
}