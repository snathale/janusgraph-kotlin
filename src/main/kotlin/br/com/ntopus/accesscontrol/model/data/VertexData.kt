package br.com.ntopus.accesscontrol.model.data

class VertexData (label: String, properties: ArrayList<Property>){
    var label: String
    var properties: ArrayList<Property>

    init {
        this.label = label
        this.properties = properties
    }

}