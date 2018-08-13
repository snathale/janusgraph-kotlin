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
        val userStorage = UserValidator().hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UCEE-002 Impossible find User with code ${this.user.code}")

        val accessRuleStorage = UserValidator().hasVertexTarget(target)
                ?: return FAILResponse(data ="@UCEE-003 Impossible find Access Rule with code ${target.code}")
        try {
            userStorage.addEdge(EdgeLabel.ASSOCIATED.label, accessRuleStorage)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UCEE-003 ${e.message.toString()}")
        }
        val localEdge = EdgeCreated(VertexInfo(VertexLabel.USER.label, this.user.code), target, EdgeLabel.ASSOCIATED.label)
        return SUCCESSResponse(data = localEdge)
    }

    override fun updateProperty(properties: List<Property>): JSONResponse {
        val user = UserValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UUPE-001 Impossible find User with code ${this.user.code}")

        if (!UserValidator().canUpdateVertexProperty(properties)) {
            return FAILResponse(data = "@UUPE-002 User property can be updated")
        }
        var values: Map<String, String> = mapOf()
        val g = graph.traversal()
        try {
            for (property in properties) {
                user.property(property.name, property.value)
            }
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UUPE-002 ${e.message.toString()}")
        }
        val traversal = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, this.user.code)
        values = AbstractMapper.parseMapVertex(traversal)
        val localUser = LocalUser(
                this.user.code,
                AbstractMapper.parseMapValue(values[PropertyLabel.NAME.label].toString()),
                AbstractMapper.formatDate(values[PropertyLabel.CREATION_DATE.label].toString()),
                AbstractMapper.parseMapValue(values[PropertyLabel.ENABLE.label].toString()).toBoolean(),
                AbstractMapper.parseMapValue((values[PropertyLabel.OBSERVATION.label].toString()))
        )
        return SUCCESSResponse(data = localUser)
    }

    override fun delete(): JSONResponse {
        val user = UserValidator()
                .hasVertex(VertexInfo(VertexLabel.USER.label, this.user.code))
                ?: return FAILResponse(data = "@UDE-001 Impossible find User with code ${this.user.code}")
        try {
            user.property(PropertyLabel.ENABLE.label, false)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = "@UDE-002 ${e.message.toString()}")
        }
        return SUCCESSResponse(data = null)
    }
}