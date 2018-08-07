package br.com.ntopus.accesscontrol.model.vertex.validator

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface IValidator {
    fun beforeInsert(vertex: ICommon): Boolean
    fun beforeUpdate(properties: List<Property>): Boolean
    fun beforeDelete(vvertex: VertexInfo): Boolean
}