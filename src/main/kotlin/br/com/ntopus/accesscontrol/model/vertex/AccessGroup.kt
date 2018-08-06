package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.*

class AccessGroup(properties: Map<String, String>): Permission(properties) {
    override fun findByCode(code: String): Common {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(): JSONResponse {
        try {
            val accessGroup = graph.addVertex("accessGroup")
            accessGroup.property("name", this.name)
            accessGroup.property("code", this.code)
            accessGroup.property("creationDate", this.creationDate)
            accessGroup.property("enable", this.enable)
            accessGroup.property("description", this.description)
            graph.tx().commit()
        } catch (e: Exception) {
            graph.tx().rollback()
            return FAILResponse(data = e.message.toString())
        }
        return SUCCESSResponse(data = this)
    }

}