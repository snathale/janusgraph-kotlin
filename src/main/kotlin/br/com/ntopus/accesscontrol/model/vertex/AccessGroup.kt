package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.interfaces.AddRelationship
import br.com.ntopus.accesscontrol.model.interfaces.InheritRelationship
import br.com.ntopus.accesscontrol.model.interfaces.RemoveRelationship
import br.com.ntopus.accesscontrol.model.vertex.base.Common
import br.com.ntopus.accesscontrol.model.vertex.base.Permission
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.Incidence

abstract class AccessGroup: Permission() {

}