package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.User
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.UserValidator
import org.janusgraph.core.JanusGraph

class UserMapper (val properties: Map<String, String>, val graph: JanusGraph): IMapper {
    val user = User(properties)
    override fun insert(): JSONResponse {
        try {
            if (!UserValidator().canInsertVertex(this.user)) {
                throw Exception("@UCVE-001 Empty User properties")
            }
            val user = graph.addVertex(VertexLabel.USER.label)
            user.property(PropertyLabel.NAME.label, this.user.name)
            user.property(PropertyLabel.CODE.label, this.user.code)
            if (!this.user.observation.isEmpty()){
                user.property(PropertyLabel.OBSERVATION.label, this.user.observation)
            }
            user.property(PropertyLabel.CREATION_DATE.label, this.user.creationDate)
            user.property(PropertyLabel.ENABLE.label, this.user.enable)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UCVE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.user)
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        if (!UserValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@UUPE-001 User not have this properties $properties")
        }
        try {
            val g = graph.traversal()
            val user = g.V().hasLabel(VertexLabel.USER.label)
            for (property in properties) {
                user.property(property.name, property.value).next()
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UUPE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.user)
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        if (!UserValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@UCEE-001 Impossible create this edge $target from User")
        }
        val user = UserValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UCEE-002 Impossible find User ${this.user}")
        val accessRule = UserValidator().hasVertexTarget(target)
                ?: return FAILResponse(data ="@UCEE-003 Impossible find Access Group $target")
        try {
            user.addE(EdgeLabel.ASSOCIATED.label).to(accessRule).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UCEE-003 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = this.user)
    }

    override fun delete(vertex: VertexInfo): JSONResponse {
        val user = UserValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UDE-001 Impossible find User ${this.user}")
        try {
            user.property(PropertyLabel.ENABLE, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }
}