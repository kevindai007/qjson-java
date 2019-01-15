# ref_map_value

```java
package testdata;
import org.qjson.any.*;

public class MyTestObject {
    
    public static Object get() {
        AnyList sameVal = new AnyList();
        return new AnyMap("a", sameVal, "b", sameVal);
    }
}
```

| value | encoded |
| ---   | ---     |
| `MyTestObject.get()` | `{"a":[],"b":"\/['a']"}` |

# ref_list_element

```java
package testdata;
import org.qjson.any.*;

public class MyTestObject {
    
    public static Object get() {
        AnyMap sameVal = new AnyMap();
        return new AnyMap("a", new AnyList(sameVal), "b", new AnyList(sameVal));
    }
}
```

| value | encoded |
| ---   | ---     |
| `MyTestObject.get()` | `{"a":[{}],"b":["\/['a'][0]"]}` |

# ref_struct_field

```java
package testdata;
import java.util.*;
public class MyStruct {
    public Object field;
}
```

```java
package testdata;
import org.qjson.any.*;

public class MyTestObject {
    
    public static Object get() {
        AnyMap sameVal = new AnyMap();
        MyStruct struct = new MyStruct();
        struct.field = sameVal;
        return new AnyMap("a", struct, "b", sameVal);
    }
}
```

| value | encoded |
| ---   | ---     |
| `MyTestObject.get()` | `{"a":{"field":{}},"b":"\/['a'].field"}` |

# ref_itself

```java
package testdata;
import org.qjson.any.*;

public class MyTestObject {
    
    public static Object get() {
        AnyMap myself = new AnyMap();
        myself.set("a", myself);
        return myself;
    }
}
```

| value | encoded |
| ---   | ---     |
| `MyTestObject.get()` | `{"a":"\/"}` |