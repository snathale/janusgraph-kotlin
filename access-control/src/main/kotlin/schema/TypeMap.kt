package schema

import org.janusgraph.core.attribute.Geoshape
import java.util.*
import kotlin.collections.HashMap

class TypeMap {
    companion object {
        val MAP: HashMap<String, Class<*>> = hashMapOf(
                "String" to String::class.java,
                "Character" to Character::class.java,
                "Boolean" to Boolean::class.java,
                "Byte" to Byte::class.java,
                "Short" to Short::class.java,
                "Integer" to Int::class.java,
                "Long" to Long::class.java,
                "Float" to Float::class.java,
                "Geoshape" to Geoshape::class.java,
                "UUID" to UUID::class.java,
                "Date" to Date::class.java
        )
    }
}