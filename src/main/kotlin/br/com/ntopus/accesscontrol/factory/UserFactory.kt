package br.com.ntopus.accesscontrol.factory

import br.com.ntopus.accesscontrol.model.vertex.Organization
import br.com.ntopus.accesscontrol.model.vertex.User
import br.com.ntopus.accesscontrol.model.vertex.base.Common

abstract class UserFactory {
    companion object {
        fun createFactory(propertyLabel: String) = when (propertyLabel) {
            "name" -> Organization::class.java
            else -> Common::class.java
        }
    }
}