package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.*
import java.util.*

class AccessRule(properties: Map<String, String>): ICommon(properties) {

//    companion object: ICommon {
//        override fun findByCode(code: String): ICommon {
//            val g = GraphFactory.open().traversal()
//            val values = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
//                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
//            val accessRule = AccessRule(hashMapOf())
//            for (item in values) {
//                accessRule.name = item.get(PropertyLabel.NAME.label).toString()
//                accessRule.code = item.get(PropertyLabel.CODE.label).toString()
//                accessRule.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
//                accessRule.observation = item.get(PropertyLabel.OBSERVATION.label).toString()
//                accessRule.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
//            }
//            return accessRule
//        }
//    }


    private fun createEdgeProvide(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createEdgeOwn(target: VertexInfo): JSONResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun createEdge(target: VertexInfo): JSONResponse {
        return when(target.label) {
            "accessGroup" -> this.createEdgeOwn(target)
            "group" -> this.createEdgeProvide(target)
            else -> ERRORResponse(message = "Impossible create a edge to $target")
        }
    }

    val expirationDate: Date = Date(properties["expirationDate"].toString())


    fun createAccessRule() {
//        try {
//            val accessRule = graph.addVertex(VertexLabel.ACCESS_RULE)
//            accessRule.property(PropertyLabel.ENABLE.toString(), this.enable)
//            accessRule.property(PropertyLabel.EXPIRATION_DATE.toString(), this.expirationDate)
//            graph.tx().commit()
//            return accessRule.id() as Long
//        } catch (e: Exception) {
//            graph.tx().rollback()
//        }
//        return -1
    }

}