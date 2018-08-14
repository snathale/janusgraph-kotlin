package br.com.ntopus.accesscontrol.model.vertex

import br.com.ntopus.accesscontrol.model.vertex.base.*
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

class AccessRule(properties: Map<String, String>): ICommon(properties) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    @SerializedName("expirationDate")
    val expirationDate: Date = if (this.toString(properties["expirationDate"]).isEmpty()) Date() else dateFormat.parse(properties["expirationDate"])

    override fun formatDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
        return format.format(this.expirationDate)
    }
//    companion object: ICommon {
//        override fun findByCode(code: String): ICommon {
//            val g = GraphFactory.open().traversal()
//            val values = g.V().hasLabel(VertexLabel.ACCESS_RULE.label)
//                    .has(PropertyLabel.CODE.label, code).valueMap<Vertex>()
//            val accessRule = AccessRule(hashMapOf())
//            for (item in values) {
//                accessRule.name = item.get(PropertyLabel.NAME.label).toString()
//                accessRule.code = item.get(PropertyLabel.CODE.label).toString()
//                accessRule.enable = item.get(PropertyLabel.ENABLE.label) as Boolean
//                accessRule.observation = item.get(PropertyLabel.OBSERVATION.label).toString()
//                accessRule.creationDate = item.get(PropertyLabel.CREATION_DATE.label) as Date
//            }
//            return accessRule
//        }
//    }


}