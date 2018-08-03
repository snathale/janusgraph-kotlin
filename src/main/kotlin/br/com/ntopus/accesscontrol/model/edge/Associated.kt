package br.com.ntopus.accesscontrol.model.edge

import br.com.ntopus.accesscontrol.model.vertex.AccessRule
import br.com.ntopus.accesscontrol.model.vertex.User
import com.syncleus.ferma.AbstractEdgeFrame
import com.syncleus.ferma.annotations.InVertex
import com.syncleus.ferma.annotations.OutVertex

abstract class Associated: AbstractEdgeFrame() {
    @InVertex
    abstract fun getIn(): User

    @OutVertex
    abstract fun getOut(): AccessRule
}