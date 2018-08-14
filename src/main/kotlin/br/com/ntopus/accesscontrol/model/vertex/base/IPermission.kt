package br.com.ntopus.accesscontrol.model.vertex.base

import com.google.gson.annotations.SerializedName
import java.util.*

abstract class IPermission(properties: Map<String, String>): IDefaultCommon(properties) {

    @SerializedName("description")
    var description: String = this.toString(properties["description"])

}