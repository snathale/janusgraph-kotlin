package br.com.ntopus.accesscontrol.model.edge

import br.com.ntopus.accesscontrol.model.vertex.base.ICommonAgent
import com.syncleus.ferma.AbstractEdgeFrame
import com.syncleus.ferma.annotations.InVertex
import com.syncleus.ferma.annotations.OutVertex

abstract class Has: AbstractEdgeFrame() {

    @InVertex
    abstract fun getIn(): ICommonAgent

    @OutVertex
    abstract fun getOut(): ICommonAgent
}