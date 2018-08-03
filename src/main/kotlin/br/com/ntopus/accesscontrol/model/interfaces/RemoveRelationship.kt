package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.Rule
import br.com.ntopus.accesscontrol.model.vertex.base.Common

interface RemoveRelationship {
    fun remove(vertex: Common)
}