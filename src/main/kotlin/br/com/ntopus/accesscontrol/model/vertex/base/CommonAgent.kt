package br.com.ntopus.accesscontrol.model.vertex.base

import java.util.*

abstract class CommonAgent(properties: Map<String, String>): Common(properties) {

    var name: String = properties["name"].toString()
    var creationDate: Date = Date()
    var observation: String = properties["observation"].toString()

}