package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.EdgeLabel
import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.User
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import br.com.ntopus.accesscontrol.model.vertex.validator.UserValidator
import java.text.SimpleDateFormat

data class LocalUser(val code: String, val name: String, val creationDate: String, val enable: Boolean, val observation: String)
class UserMapper (val properties: Map<String, String>): IMapper {

    private val user = User(properties)

    private val graph = GraphFactory.open()
    override fun insert(): JSONResponse {
        try {
            if (!UserValidator().canInsertVertex(this.user)) {
                return FAILResponse(data = "@UCVE-001 Empty User properties")
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
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val localUser = LocalUser(this.user.code, this.user.name, format.format(this.user.creationDate), this.user.enable, this.user.observation)
        return SUCCESSResponse(data = localUser)
    }

    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
        if (!UserValidator().isCorrectVertexTarget(target)) {
            return FAILResponse(data = "@UCEE-001 Impossible create this edge $target from User")
        }
        val user = UserValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UCEE-002 Impossible find User ${this.user}")
        if (UserValidator().hasVertexTarget(target) != null) {
            val t = "null"
        }
        val accessRule = (UserValidator().hasVertexTarget(target))
                ?: return FAILResponse(data ="@UCEE-003 Impossible find Access Rule $target")
        try {
            user.addE(EdgeLabel.ASSOCIATED.label).to(accessRule).next()
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UCEE-003 ${e.message.toString()}")
        }
        val localEdge = EdgeCreated(VertexInfo(VertexLabel.USER.label, this.user.code), target, EdgeLabel.ASSOCIATED.label)
        return SUCCESSResponse(data = localEdge)
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

//    override fun createEdge(target: VertexInfo, edgeLabel: String?): JSONResponse {
//        if (!UserValidator().isCorrectVertexTarget(target)) {
//            return FAILResponse(data = "@UCEE-001 Impossible create this edge $target from User")
//        }
//        val user = UserValidator()
//                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
//                ?: return FAILResponse(data = "@UCEE-002 Impossible find User ${this.user}")
//        val accessRule = UserValidator().hasVertexTarget(target)
//                ?: return FAILResponse(data ="@UCEE-003 Impossible find Access Rule $target")
//        try {
//            user.addE(EdgeLabel.ASSOCIATED.label).to(accessRule).next()
//            graph.tx().commit()
//        } catch (e: Exception) {
//            graph.tx().rollback()
//            return FAILResponse(data = "@UCEE-003 ${e.message.toString()}")
//        }
//        val localEdge = EdgeCreated(VertexInfo(VertexLabel.USER.label, this.user.code), target, EdgeLabel.ASSOCIATED.label)
//        return SUCCESSResponse(data = localEdge)
//    }

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