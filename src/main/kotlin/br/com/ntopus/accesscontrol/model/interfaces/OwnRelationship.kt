package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.Common

interface OwnRelationship {
    fun own(vertex: Common)
}