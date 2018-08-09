package br.com.ntopus.accesscontrol.factory

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.*

abstract class MapperFactory {

    companion object {
        fun createFactory(vertex: VertexData): IMapper = when(vertex.label) {
            VertexLabel.USER.label -> UserMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.ORGANIZATION.label -> OrganizationMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.UNIT_ORGANIZATION.label -> UnitOrganizationMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.GROUP.label -> GroupMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.RULE.label -> RuleMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.ACCESS_GROUP.label -> AccessGroupMapper(vertex.properties.associateBy({it.name}, {it.value}))
            VertexLabel.ACCESS_RULE.label -> AccessRuleMapper(vertex.properties.associateBy({it.name}, {it.value}))
            else -> throw IllegalArgumentException()

        }

    }
}