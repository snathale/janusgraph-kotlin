import org.janusgraph.core.ConfiguredGraphFactory

fun create () {
    try {
        val graph = ConfiguredGraphFactory.create("access-control-test")
        graph.tx().commit()
    }catch (e: Exception) {

    }

}

