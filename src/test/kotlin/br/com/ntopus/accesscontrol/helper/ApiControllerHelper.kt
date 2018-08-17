package br.com.ntopus.accesscontrol.helper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.*
import org.junit.Assert
import java.text.SimpleDateFormat
import java.util.*

data class CreateAgentSuccess(val status: String, val data: AgentResponse)
data class CreatePermissionSuccess(val status: String, val data: PermissionResponse)
data class CreateAssociationSuccess(val status: String, val data: AssociationResponse)
data class CreateEdgeSuccess(val status: String, val data: EdgeCreated)
abstract class ApiControllerHelper {

    fun createVertexBaseUrl(port: Int): String {
        return "http://localhost:$port/api/v1/vertex"
    }

    fun createEdgeBaseUrl(port: Int): String {
        return "http://localhost:$port/api/v1/edge"
    }

    fun createDefaultOrganization(date: Date) {
        val graph = GraphFactory.open()
        try {
            val organization = graph.addVertex(VertexLabel.ORGANIZATION.label)
            organization.property(PropertyLabel.NAME.label, "Kofre")
            organization.property(PropertyLabel.CODE.label, 1)
            organization.property(PropertyLabel.OBSERVATION.label, "This is a Organization")
            organization.property(PropertyLabel.CREATION_DATE.label, date)
            organization.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultUnitOrganization(date: Date) {
        val graph = GraphFactory.open()
        try {
            val unitOrganization = graph.addVertex(VertexLabel.UNIT_ORGANIZATION.label)
            unitOrganization.property(PropertyLabel.NAME.label, "Bahia")
            unitOrganization.property(PropertyLabel.CODE.label, 1)
            unitOrganization.property(PropertyLabel.OBSERVATION.label, "This is a Unit Organization")
            unitOrganization.property(PropertyLabel.CREATION_DATE.label, date)
            unitOrganization.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultGroup(date: Date) {
        val graph = GraphFactory.open()
        try {
            val group1 = graph.addVertex(VertexLabel.GROUP.label)
            group1.property(PropertyLabel.NAME.label, "Marketing")
            group1.property(PropertyLabel.CODE.label, 1)
            group1.property(PropertyLabel.OBSERVATION.label, "This is a Marketing Group")
            group1.property(PropertyLabel.CREATION_DATE.label, date)
            group1.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultUser(date: Date) {
        val graph = GraphFactory.open()
        try {
            val user = graph.addVertex(VertexLabel.USER.label)
            user.property(PropertyLabel.NAME.label, "UserTest")
            user.property(PropertyLabel.CODE.label, 1)
            user.property(PropertyLabel.OBSERVATION.label, "This is UserTest")
            user.property(PropertyLabel.CREATION_DATE.label, date)
            user.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultAccessRule(date: Date) {
        val graph = GraphFactory.open()
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE.label)
            accessRule.property(PropertyLabel.CODE.label, 1)
            accessRule.property(PropertyLabel.ENABLE.label, true)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, date)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultAccessGroup(date: Date) {
        val graph = GraphFactory.open()
        try {
            val unitOrganization = graph.addVertex(VertexLabel.ACCESS_GROUP.label)
            unitOrganization.property(PropertyLabel.NAME.label, "Operator")
            unitOrganization.property(PropertyLabel.CODE.label, 1)
            unitOrganization.property(PropertyLabel.DESCRIPTION.label, "This is a Operator Access Group")
            unitOrganization.property(PropertyLabel.CREATION_DATE.label, date)
            unitOrganization.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun createDefaultRules(date: Date) {
        val graph = GraphFactory.open()
        try {
            val rule1 = graph.addVertex(VertexLabel.RULE.label)
            rule1.property(PropertyLabel.NAME.label, "ADD_USER")
            rule1.property(PropertyLabel.CODE.label, 1)
            rule1.property(PropertyLabel.DESCRIPTION.label, "This is a Rule Add User")
            rule1.property(PropertyLabel.CREATION_DATE.label, date)
            rule1.property(PropertyLabel.ENABLE.label, true)
            val rule2 = graph.addVertex(VertexLabel.RULE.label)
            rule2.property(PropertyLabel.NAME.label, "EDIT_USER")
            rule2.property(PropertyLabel.CODE.label, 2)
            rule2.property(PropertyLabel.DESCRIPTION.label, "This is a Rule Edit User")
            rule2.property(PropertyLabel.CREATION_DATE.label, this.addMinutes(date, 1))
            rule2.property(PropertyLabel.ENABLE.label, true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
        }
    }

    fun addDays(date: Date, days: Int): Date {
        val cal = GregorianCalendar()
        cal.setTime(date)
        cal.add(Calendar.DATE, days)
        return cal.getTime()
    }

    fun addMinutes(date: Date, minutes: Int): Date {
        val cal = GregorianCalendar()
        cal.setTime(date)
        cal.add(Calendar.MINUTE, minutes)
        return cal.getTime()
    }

    fun assertEdgeCreatedSuccess(source: VertexInfo, target: VertexInfo, response: CreateEdgeSuccess, edgeLabel: String) {
        Assert.assertEquals("SUCCESS", response.status)
        Assert.assertEquals(source.label, response.data.source.label)
        Assert.assertEquals(source.code, response.data.source.code)
        Assert.assertEquals(target.label, response.data.target.label)
        Assert.assertEquals(target.code, response.data.target.code)
        Assert.assertEquals(edgeLabel, response.data.edgeLabel)
    }

    fun assertUserMapper(code: String, name: String, creationDate: Date, observation: String, enable: Boolean) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val g = GraphFactory.open().traversal()
        val userStorage = g.V().hasLabel("user").has("code", code)
        val values = AbstractMapper.parseMapVertex(userStorage)
        Assert.assertEquals(name, AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals(code, AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(observation, AbstractMapper.parseMapValue(values["observation"].toString()))
        Assert.assertEquals(format.format(creationDate), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(enable, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    fun assertUserApiResponseSuccess(code: String, name: String, creationDate: Date, observation: String, enable: Boolean, response: CreateAgentSuccess) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        Assert.assertEquals("SUCCESS", response.status)
        Assert.assertEquals(format.format(creationDate), format.format(format.parse(response.data.creationDate)))
        Assert.assertEquals(name, response.data.name)
        Assert.assertEquals(code, response.data.code)
        Assert.assertEquals(observation, response.data.observation)
        Assert.assertEquals(enable, response.data.enable)
    }

    fun assertAccessRuleApiResponseSuccess(code: String, enable: Boolean, expirationDate: Date, response: CreateAssociationSuccess) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val objExpirationDate = format.parse(response.data.expirationDate)
        Assert.assertEquals("SUCCESS", response.status)
        Assert.assertEquals(format.format(expirationDate), format.format(objExpirationDate))
        Assert.assertEquals(code, response.data.code)
        Assert.assertEquals(enable, response.data.enable)
    }

    fun assertAccessRuleMapper(code: String, enable: Boolean, expirationDate: Date? = null) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val g = GraphFactory.open().traversal()
        val vertex = g.V().hasLabel("accessRule").has("code", code)
        val values = AbstractMapper.parseMapVertex(vertex)
        if (expirationDate!=null){
            Assert.assertEquals(format.format(expirationDate), AbstractMapper.parseMapValueDate(values["expirationDate"].toString()))
        }
        Assert.assertEquals(code, AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(enable, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    fun assertAccessGroupMapper(code: String, name: String, description: String, creationDate: Date, enable: Boolean) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val g = GraphFactory.open().traversal()
        val vertex = g.V().hasLabel("accessGroup").has("code", code)
        val values = AbstractMapper.parseMapVertex(vertex)
        Assert.assertEquals(format.format(creationDate), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(name, AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals(code, AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(description, AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(enable, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }

    fun assertAccessGroupResponseSuccess(code: String, name: String, enable: Boolean, creationDate: Date, description: String, response: CreatePermissionSuccess) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val objDate = format.parse(response.data.creationDate)
        Assert.assertEquals("SUCCESS", response.status)
        Assert.assertEquals(format.format(creationDate), format.format(objDate))
        Assert.assertEquals(code, response.data.code)
        Assert.assertEquals(name, response.data.name)
        Assert.assertEquals(enable, response.data.enable)
        Assert.assertEquals(description, response.data.description)
    }

    fun assertRuleMapper(code: String, name: String, description: String, creationDate: Date, enable: Boolean) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val g = GraphFactory.open().traversal()
        val vertex = g.V().hasLabel("rule").has("code", code)
        val values = AbstractMapper.parseMapVertex(vertex)
        Assert.assertEquals(format.format(creationDate), AbstractMapper.parseMapValueDate(values["creationDate"].toString()))
        Assert.assertEquals(name, AbstractMapper.parseMapValue(values["name"].toString()))
        Assert.assertEquals(code, AbstractMapper.parseMapValue(values["code"].toString()))
        Assert.assertEquals(description, AbstractMapper.parseMapValue(values["description"].toString()))
        Assert.assertEquals(enable, AbstractMapper.parseMapValue(values["enable"].toString()).toBoolean())
    }


    fun assertRuleApiResponseSuccess(code: String, name: String, description: String, enable: Boolean, creationDate: Date, response: CreatePermissionSuccess) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        val objExpirationDate = format.parse(response.data.creationDate)
        Assert.assertEquals("SUCCESS", response.status)
        Assert.assertEquals(format.format(creationDate), format.format(objExpirationDate))
        Assert.assertEquals(code, response.data.code)
        Assert.assertEquals(name, response.data.name)
        Assert.assertEquals(description, response.data.description)
        Assert.assertEquals(enable, response.data.enable)
    }
}