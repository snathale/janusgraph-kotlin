package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.interfaces.AddRelationship
import br.com.ntopus.accesscontrol.model.interfaces.ProvideRelationship
import br.com.ntopus.accesscontrol.model.vertex.base.Common
import com.syncleus.ferma.annotations.Adjacency
import com.syncleus.ferma.annotations.Incidence
import com.syncleus.ferma.annotations.Property
import java.util.*

abstract class AccessRule: Common(), AddRelationship, ProvideRelationship {
    @Property("expirationDate")
    abstract fun getExpirationDate(): Date

    @Property("expirationDate")
    abstract fun setExpirationDate(expirationDate: Date)

    @Incidence(label = "own")
    abstract override fun add(vertex: Common)

    @Adjacency(label = "provide")
    abstract override fun provide(vertex: Common)

}