package br.com.ntopus.accesscontrol.helper

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.AgentResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.AssociationResponse
import br.com.ntopus.accesscontrol.model.vertex.mapper.EdgeCreated
import java.util.*

data class CreateAgentSuccess(val status: String, val data: AgentResponse)
data class CreateAssociationSuccess(val status: String, val data: AssociationResponse)
data class CreateEdgeSuccess(val status: String, val data: EdgeCreated)
abstract class ApiControllerHerper {

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
            organization.property(PropertyLabel.CODE.label, "1")
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
            unitOrganization.property(PropertyLabel.CODE.label, "1")
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
            group1.property(PropertyLabel.CODE.label, "1")
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
            user.property(PropertyLabel.CODE.label, "1")
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
            accessRule.property(PropertyLabel.CODE.label, "1")
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
            unitOrganization.property(PropertyLabel.CODE.label, "1")
            unitOrganization.property(PropertyLabel.DESCRIPTION.label, "This is a Operator Access Group")
            unitOrganization.property(PropertyLabel.CREATION_DATE.label, date)
            unitOrganization.property(PropertyLabel.ENABLE.label, true)
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
}