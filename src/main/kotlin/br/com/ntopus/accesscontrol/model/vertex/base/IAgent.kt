package br.com.ntopus.accesscontrol.model.vertex.base

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

abstract class IAgent(properties: Map<String, String>): IDefaultCommon(properties) {

    @SerializedName("observation")
    var observation: String = this.toString(properties["observation"])

}