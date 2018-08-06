package br.com.ntopus.accesscontrol.model.vertex.base

import java.util.*

abstract class Permission(properties: Map<String, String>): Common(properties) {

    var name: String = properties["name"].toString()
    var creationDate: Date = Date()
    var description: String = properties["description"].toString()
}