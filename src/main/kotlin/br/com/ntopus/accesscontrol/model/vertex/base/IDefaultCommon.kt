package br.com.ntopus.accesscontrol.model.vertex.base

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

abstract class IDefaultCommon(properties: Map<String, String>): ICommon(properties) {

    @SerializedName("creationDate")
    var creationDate: Date = Date()

    @SerializedName("name")
    var name: String = this.toString(properties["name"])

    override fun formatDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        return format.format(this.creationDate)
    }

}