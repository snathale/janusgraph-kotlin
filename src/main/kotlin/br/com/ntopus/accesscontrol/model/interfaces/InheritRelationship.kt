package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.AccessGroup

interface InheritRelationship {
    fun inherit(vertex: AccessGroup)
}