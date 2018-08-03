package br.com.ntopus.accesscontrol.model.data

enum class EdgeLabel(val label: String) {
    HAS("has"),
    PROVIDE("provide"),
    OWN("own"),
    ADD("add"),
    REMOVE("remove"),
    ASSOCIATED("associated"),
    INHERIT("inherit")
}