package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface ProvideRelationship {
    fun provide(vertex: ICommon)
}