package br.com.ntopus.accesscontrol.model.interfaces

import br.com.ntopus.accesscontrol.model.vertex.base.Common
import jdk.nashorn.internal.runtime.Property

interface AddProperty {
    fun setProperty(vertex: Property)
}