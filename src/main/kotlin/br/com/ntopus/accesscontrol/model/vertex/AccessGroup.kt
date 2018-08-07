package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.PropertyLabel
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.base.*
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.util.*

class AccessGroup(properties: Map<String, String>): IPermission(properties) {
//    override fun createEdge(target: VertexInfo): JSONResponse {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    companion object {
        fun findByCode(code: String): ICommon {
            val g = GraphFactory.open().traversal()
            val values = g.V().hasLabel(VertexLabel.ACCESS_GROUP.label)
                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
            val acce = AccessGroup(hashMapOf())
            for (item in values) {
                acce.name = item.get(PropertyLabel.NAME.label).toString()
                acce.code = item.get(PropertyLabel.CODE.label).toString()
                acce.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
                acce.description = item.get(PropertyLabel.DESCRIPTION.label).toString()
                acce.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
            }
            return acce
        }
    }
//
//    override fun insert(): JSONResponse {
//        try {
//            val accessGroup = graph.addVertex("accessGroup")
//            accessGroup.property("name", this.name)
//            accessGroup.property("code", this.code)
//            accessGroup.property("creationDate", this.creationDate)
//            accessGroup.property("enable", this.enable)
//            accessGroup.property("description", this.description)
//            graph.tx().commit()
//        } catch (e: Exception) {
//            graph.tx().rollback()
//            return FAILResponse(data = e.message.toString())
//        }
//        return SUCCESSResponse(data = this)
//    }

}