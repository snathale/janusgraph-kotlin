package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.interfaces.VertexInfo
import br.com.ntopus.accesscontrol.model.vertex.base.ICommon
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse

interface IMapper {
    fun insert (): JSONResponse
    fun updateProperty (properties: List<Property>): JSONResponse
    fun createEdge(target: VertexInfo): JSONResponse
    fun delete (vertex: VertexInfo): JSONResponse
}