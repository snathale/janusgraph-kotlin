package br.com.ntopus.accesscontrol.model.edge

import br.com.ntopus.accesscontrol.model.vertex.AccessGroup
import com.syncleus.ferma.AbstractEdgeFrame
import com.syncleus.ferma.annotations.InVertex
import com.syncleus.ferma.annotations.OutVertex

abstract class Inherit: AbstractEdgeFrame() {

    @InVertex
    abstract fun getIn(): AccessGroup

    @OutVertex
    abstract fun getOut(): AccessGroup
}