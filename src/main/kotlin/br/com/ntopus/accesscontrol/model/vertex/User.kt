package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.*

class User(properties: Map<String, String>): ICommonAgent(properties) {

//    companion object {
//        override fun findByCode(code: String): ICommon {
//            val g = GraphFactory.open().traversal()
//            val values = g.V().hasLabel(VertexLabel.USER.label)
//                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
//            val user = User(hashMapOf())
//            for (item in values) {
//                user.name = item.get(PropertyLabel.NAME.label).toString()
//                user.code = item.get(PropertyLabel.CODE.label).toString()
//                user.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
//                user.observation = item.get(PropertyLabel.OBSERVATION.label).toString()
//                user.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
//            }
//            return user
//        }
//    }

//    override fun createEdge(target: VertexInfo): JSONResponse {
//        if (!target.label.equals(VertexLabel.ACCESS_GROUP.label)) {
//            return FAILResponse(data = "Impossible create this edge $target from User")
//        }
//
//        val g = graph.traversal()
//        val user = g.V().hasLabel(VertexLabel.USER.label).has(PropertyLabel.CODE.label, this.code)
//        if ( user == null) {
//            return FAILResponse(data = "Impossible find User ${this}")
//        }
//
//        val accessRule = g.V().hasLabel(VertexLabel.ACCESS_RULE.label).has(PropertyLabel.CODE.label, target.code)
//        if (accessRule == null) {
//            return FAILResponse(data = "Impossible find Access Group $target")
//        }
//
//        try {
//            user.addE(EdgeLabel.ASSOCIATED.label).to(accessRule).next()
//            graph.tx().commit()
//        } catch (e: Exception) {
//            g.tx().rollback()
//            return FAILResponse(data = e.message.toString())
//        }
//        return SUCCESSResponse(data = this)
//    }

//    override fun insert(vertex: Vertex): JSONResponse {
//        try {
//            val user = graph.addVertex(VertexLabel.USER)
//            user.property(PropertyLabel.NAME.label, this.name)
//            user.property(PropertyLabel.CODE.label, this.code)
//            user.property(PropertyLabel.OBSERVATION.label, this.observation)
//            user.property(PropertyLabel.CREATION_DATE.label, this.creationDate)
//            user.property(PropertyLabel.ENABLE.label, true)
//            graph.tx().commit()
//        } catch (e: Exception) {
//            graph.tx().rollback()
//            return FAILResponse(data = e.message.toString())
//        }
//        return SUCCESSResponse(data = this)
//    }
}