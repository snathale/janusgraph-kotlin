package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface RemoveRelationship {
    fun remove(vertex: ICommon)
}