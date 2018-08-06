package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.CommonAgent
import br.com.ntopus.accesscontrol.model.vertex.base.FAILResponse
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import br.com.ntopus.accesscontrol.model.vertex.base.SUCCESSResponse
import java.util.*


class Group(properties: Map<String, String>): CommonAgent(properties) {
    override fun insert(): JSONResponse {
        try {
            val group = graph.addVertex("group")
            group.property("name", this.name)
            group.property("code", this.code)
            group.property("observation", this.observation)
            group.property("creationDate", Date())
            group.property("enable", true)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }
}