package br.com.ntopus.accesscontrol.model.data

class Property(name: String, value: String) {
    var name: String
    var value: String

    init {
        this.name = name
        this.value = value
    }
}