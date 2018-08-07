package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface OwnRelationship {
    fun own(vertex: ICommon)
}