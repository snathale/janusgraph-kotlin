package br.com.ntopus.accesscontrol.model.data

class VertexData (label: String, properties: List<Property>){
    var label: String
    var properties: List<Property>

    init {
        this.label = label
        this.properties = properties
    }
}