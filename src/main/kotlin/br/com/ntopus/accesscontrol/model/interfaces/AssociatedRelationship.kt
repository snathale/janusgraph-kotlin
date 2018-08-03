package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.Common

interface AssociatedRelationship {
    fun associated(vertex: Common)
}