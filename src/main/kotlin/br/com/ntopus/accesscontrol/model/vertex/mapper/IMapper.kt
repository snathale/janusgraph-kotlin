package br.com.ntopus.accesscontrol.model.vertex.mapper

import br.com.ntopus.accesscontrol.model.data.Property
import br.com.ntopus.accesscontrol.model.vertex.base.JSONResponse
import com.google.gson.annotations.SerializedName

data class EdgeCreated(
        @SerializedName("source") val source: VertexInfo,
        @SerializedName("target") val target: VertexInfo,
        @SerializedName("edgeLabel") val edgeLabel: String
)

data class VertexInfo(
        @SerializedName("label") val label: String,
        @SerializedName("code") val code: String
)
interface IMapper {
    fun insert (): JSONResponse
    fun updateProperty (properties: List<Property>): JSONResponse
    fun createEdge(target: VertexInfo, edgeLabel: String? = ""): JSONResponse
    fun delete (): JSONResponse
}