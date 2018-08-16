package br.com.ntopus.accesscontrol.model.vertex.mapper.factory

import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.VertexInfo

class AccessGroupEdgeFactory {
    fun edgeForTarget(target: VertexInfo, edgeLabel: String): ICreateEdge? {
        return when (target.label) {
            VertexLabel.ACCESS_GROUP.label -> EdgeFromAccessGroup()
            VertexLabel.RULE.label -> EdgeFromRule(edgeLabel)
            else -> null
        }
    }
}