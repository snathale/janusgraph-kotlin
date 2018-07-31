package validator

import data.PropertyKey
import data.PropertyKeyLabel
import data.VertexLabel
import kotlin.system.exitProcess

abstract class VertexPropertyKey {
    companion object {
        fun isPropertyIsCorrect(label: String, propertyKey: PropertyKey): Boolean {
            if (label.equals(VertexLabel.ORGANIZATION.label) || label.equals(VertexLabel.UNIT_ORGANIZATION.label) ||
                    label.equals(VertexLabel.GROUP.label) || label.equals(VertexLabel.USER.label)) {
                if (propertyKey.name.equals(PropertyKeyLabel.ID.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.NAME.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.CODE.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.OBSERVATION.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.ENABLE.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.CREATION_DATE.label)
                ) {
                    return true
                }
            }
            if (label.equals(VertexLabel.ACCESS_GROUP.label) || label.equals(VertexLabel.RULE.label)) {
                if (propertyKey.name.equals(PropertyKeyLabel.ID.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.NAME.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.DESCRIPTION.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.ENABLE.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.CODE.label) ||
                    propertyKey.name.equals(PropertyKeyLabel.CREATION_DATE.label)
                ) {
                    return true
                }
            }
            if (label.equals(VertexLabel.ACCESS_RULE.label)) {
                if (propertyKey.name.equals(PropertyKeyLabel.EXPIRATION_DATE.label)) {
                    return true
                }
            }
            println("[SCHEMA] Impossible add the property ${propertyKey.name} to label $label")
            exitProcess(1)
        }
    }
}