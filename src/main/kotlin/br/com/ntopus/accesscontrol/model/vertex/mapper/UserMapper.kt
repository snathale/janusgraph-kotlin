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
            if (!UserValidator().beforeInsert(this.user)) {
                throw Exception("Empty User properties")
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
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this.user)
    }

    override fun updateProperty(vertex: VertexInfo, property: Property): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEdge(target: VertexInfo): JSONResponse {
        if (!target.label.equals(VertexLabel.ACCESS_GROUP.label)) {
            return FAILResponse(data = "Impossible create this edge $target from User")
        }

        val g = graph.traversal()
        val user = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, this.user.code)
        if ( user == null) {
            return FAILResponse(data = "Impossible find User $this.user")
        }

        val accessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, target.code)
        if (accessRule == null) {
            return FAILResponse(data = "Impossible find Access Group $target")
        }

        try {
            user.addE(EdgeLabel.ASSOCIATED.label).to(accessRule).next()
            graph.tx().commit()
        } catch (e: Exception) {
            g.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    override fun delete(vertex: VertexInfo, code: String): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}