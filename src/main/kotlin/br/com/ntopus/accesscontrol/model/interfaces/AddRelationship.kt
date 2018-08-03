package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.Common

interface AddRelationship {
    fun add(vertex: Common)
}