package data

class EdgeData (label: String, source: Long, target: Long) {
    val label: String
    val source: Long
    val target: Long
    init {
        this.label = label
        this.source = source
        this.target = target
    }
}