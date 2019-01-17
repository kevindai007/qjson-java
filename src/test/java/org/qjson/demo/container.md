# Container

QJSON like JSON, only support `[]` or `{}` as container. 
Java container class will map to `[]` or `{}`.

# builtin_container

built-in container is supported out of box

* Iterable(Collection/Set/List) => []
* Array => []
* Map => {}

```java
Assert.assertEquals("[true]", QJSON.stringify(
        new ArrayList(){{ 
            add(true); 
        }}));
Assert.assertEquals("[true]", QJSON.stringify(
        new HashSet(){{ 
            add(true);
        }}));
Assert.assertEquals("[true]", QJSON.stringify(
        new boolean[]{true}));
Assert.assertEquals("[true]", QJSON.stringify(
        new Object[]{true}));
Assert.assertEquals("{\"a\":true}", QJSON.stringify(
        new HashMap(){{ 
            put("a", true); 
        }}));
```

If decode back without specifying type, the default mapping is 

* [] => org.qjson.any.AnyList
* {} => org.qjson.any.AnyMap


```java
org.qjson.any.Any anyList = QJSON.parse("[[true],[false]]");
Assert.assertEquals(false, anyList.get(1, 0));
org.qjson.any.Any anyMap = QJSON.parse("{\"a\":[[false]]}");
Assert.assertEquals(false, anyMap.get("a", 0, 0));
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import java.util.*;
import org.junit.Assert;

public class Demo {
    
    public static void demo() {
        QJSON qjson = new QJSON();
        {{ CODE }}
    }
}
```

</hide>

# user_defined_container