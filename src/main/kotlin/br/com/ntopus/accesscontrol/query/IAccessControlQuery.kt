package br.com.ntopus.accesscontrol.query

import br.com.ntopus.accesscontrol.model.vertex.base.ICommon

interface IAccessControlQuery {
    fun findByCode(code: String): ICommon
}