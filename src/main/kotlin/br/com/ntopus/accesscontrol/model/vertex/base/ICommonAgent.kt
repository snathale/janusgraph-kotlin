package br.com.ntopus.accesscontrol.model.vertex.base

import com.google.gson.annotations.SerializedName
import java.util.*

abstract class ICommonAgent(properties: Map<String, String>): ICommon(properties) {

    @SerializedName("creationDate")
    var creationDate: Date = Date()

    @SerializedName("observation")
    var observation: String = properties["observation"].toString()
}