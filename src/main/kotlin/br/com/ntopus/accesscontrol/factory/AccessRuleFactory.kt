package br.com.ntopus.accesscontrol.factory

import br.com.ntopus.accesscontrol.model.data.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.AccessRule
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse

class AccessRuleFactory(val properties: Map<String, String>, val source: VertexInfo, val target: VertexInfo) {

    fun createEdgeForAccessRule(targetLabel: String): JSONResponse? {
        return when(targetLabel) {
            "group" -> AccessRule(properties).createEdgeProvide(source, target)
            "own" -> AccessRule(properties).createEdgeOwn(source, target)
            else -> null

        }
    }
}