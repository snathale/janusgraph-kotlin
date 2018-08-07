package br.com.ntopus.accesscontrol.factory

import br.com.ntopus.accesscontrol.model.GraphFactory
import br.com.ntopus.accesscontrol.model.data.VertexData
import br.com.ntopus.accesscontrol.model.data.VertexLabel
import br.com.ntopus.accesscontrol.model.vertex.mapper.*

abstract class MapperFactory {

    companion object {
        fun createFactory(vertex: VertexData): IMapper = when(vertex.label) {
            VertexLabel.USER.label -> UserMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.ORGANIZATION.label -> OrganizationMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.UNIT_ORGANIZATION.label -> UnitOrganizationMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.GROUP.label -> GroupMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.RULE.label -> RuleMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.ACCESS_GROUP.label -> AccessGroupMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            VertexLabel.ACCESS_RULE.label -> AccessRuleMapper(vertex.properties.associateBy({it.name}, {it.value}), GraphFactory.open())
            else -> throw IllegalArgumentException()

        }

    }
}