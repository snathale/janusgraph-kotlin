package br.com.ntopus.accesscontrol.model.edge

import br.com.ntopus.accesscontrol.model.vertex.AccessGroup
import br.com.ntopus.accesscontrol.model.vertex.Rule
import com.syncleus.ferma.AbstractEdgeFrame
import com.syncleus.ferma.annotations.InVertex
import com.syncleus.ferma.annotations.OutVertex

abstract class Remove: AbstractEdgeFrame() {
    @InVertex
    abstract fun getIn(): AccessGroup

    @OutVertex
    abstract fun getOut(): Rule
}