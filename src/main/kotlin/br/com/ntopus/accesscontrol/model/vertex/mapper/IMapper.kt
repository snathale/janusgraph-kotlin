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

data class AgentResponse(
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String,
        @SerializedName("creationDate") val creationDate: String,
        @SerializedName("enable") val enable: Boolean,
        @SerializedName("observation") val observation: String
)

data class PermissionResponse(
        @SerializedName("code") val code: String,
        @SerializedName("name") val name: String,
        @SerializedName("creationDate") val creationDate: String,
        @SerializedName("description") val description: String,
        @SerializedName("enable") val enable: Boolean
)

data class AssociationResponse(
        @SerializedName("code") val code: String,
        @SerializedName("expirationDate") val expirationDate: String?,
        @SerializedName("enable") val enable: Boolean
)

interface IMapper {
    fun insert(): JSONResponse
    fun updateProperty(properties: List<Property>): JSONResponse
    fun createEdge(target: VertexInfo): JSONResponse
    fun delete(): JSONResponse
}