package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.Common

interface ProvideRelationship {
    fun provide(vertex: Common)
}