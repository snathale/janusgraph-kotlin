package schema;

import org.janusgraph.core.attribute.Geoshape;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TypeMap {
    public static HashMap<String, Class> MAP = new HashMap<String, Class>();

    static {
        MAP.put("String", String.class);
        MAP.put("Character", Character.class);
        MAP.put("Boolean", Boolean.class);
        MAP.put("Byte", Byte.class);
        MAP.put("Short", Short.class);
        MAP.put("Integer", Integer.class);
        MAP.put("Long", Long.class);
        MAP.put("Float", Float.class);
        MAP.put("Geoshape", Geoshape.class);
        MAP.put("UUID", UUID.class);
        MAP.put("Date", Date.class);
    };
}
