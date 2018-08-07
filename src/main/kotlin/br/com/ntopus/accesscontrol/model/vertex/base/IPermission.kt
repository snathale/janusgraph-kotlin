package br.com.ntopus.accesscontrol.model.vertex.base

import com.google.gson.annotations.SerializedName
import java.util.*

abstract class IPermission(properties: Map<String, String>): ICommon(properties) {

    @SerializedName("creationDate")
    var creationDate: Date = Date()

    @SerializedName("description")
    var description: String = properties["description"].toString()
}