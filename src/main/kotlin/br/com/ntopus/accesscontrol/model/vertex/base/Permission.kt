package br.com.ntopus.accesscontrol.model.vertex.base

import java.util.*

abstract class Permission: Common() {

    val name: String = ""
    val creationDate: Date? = null
    val description: String = ""
}