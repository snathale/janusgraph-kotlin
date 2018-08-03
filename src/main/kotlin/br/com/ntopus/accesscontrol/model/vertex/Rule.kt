package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.interfaces.AddRelationship
import br.com.ntopus.accesscontrol.model.vertex.base.Common
import br.com.ntopus.accesscontrol.model.vertex.base.Permission
import com.syncleus.ferma.annotations.Incidence

abstract class Rule: Permission(), AddRelationship{

    @Incidence(label = "has")
    abstract override fun add(vertex: Common)
}