package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface AssociatedRelationship {
    fun associated(vertex: ICommon)
}