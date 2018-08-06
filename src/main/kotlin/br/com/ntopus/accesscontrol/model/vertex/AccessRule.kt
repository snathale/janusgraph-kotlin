package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.factory.AccessRuleRelationship
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexInfo
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.Common
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import java.util.*

class AccessRule(properties: Map<String, String>): Common(properties), AccessRuleRelationship {
    override fun createEdgeProvide(source: VertexInfo, target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createEdgeOwn(source: VertexInfo, target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val expirationDate: Date = Date(properties["expirationDate"].toString())

    override fun insert(): JSONResponse {
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE)
            accessRule.property(PropertyLabel.CODE.label, this.code)
            accessRule.property(PropertyLabel.ENABLE.label, this.enable)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.label, this.expirationDate)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

    fun createAccessRule(): Long {
        try {
            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE)
            accessRule.property(PropertyLabel.ENABLE.toString(), this.enable)
            accessRule.property(PropertyLabel.EXPIRATION_DATE.toString(), this.expirationDate)
            graph.tx().commit()
            return accessRule.id() as Long
        } catch (e: Exception) {
            graph.tx().rollback()
        }
        return -1
    }

//    @Property("expirationDate")
//    abstract fun getExpirationDate(): Date
//
//    @Property("expirationDate")
//    abstract fun setExpirationDate(expirationDate: Date)
//
//    @Incidence(label = "own")
//    abstract override fun createEdge(vertex: Common)
//
//    @Adjacency(label = "provide")
//    abstract override fun provide(vertex: Common)

}