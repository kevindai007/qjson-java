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
| `MyTestObject.get()` | `{"a":[],"b":"\\['a']"}` |