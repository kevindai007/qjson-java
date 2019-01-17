# Container

QJSON like JSON, only support `[]` or `{}` as container. 
Java container class will map to `[]` or `{}`.

# builtin_container

built-in container is supported out of box. The mapping relationship is like this

| java type | QJSON |
| ---   | --- |
| Iterable(Collection/Set/List) | `[]` |
| Array | `{}` |
| Map  | `{}` |

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

| QJSON | java type |
| --- | --- |
| `[]` | org.qjson.any.AnyList |
| `{}` | org.qjson.any.AnyMap |

```java
org.qjson.any.Any anyList = QJSON.parse("[[true],[false]]");
Assert.assertEquals(false, anyList.get(1, 0));
org.qjson.any.Any anyMap = QJSON.parse("{\"a\":[[false]]}");
Assert.assertEquals(false, anyMap.get("a", 0, 0));
```

To decode specified type, we need to pass in the class


```java
QJSON qjson = new QJSON();
List list = qjson.decode(ArrayList.class, "[true]");
Assert.assertEquals(Arrays.asList(true), list);
```

However, due to the limit of Java generic type, we can not know the element type of the list.
We have to use this syntax to specify the accurate collection type:

```java
QJSON qjson = new QJSON();
List list = qjson.decode(new TypeLiteral<ArrayList<ArrayList>>(){}, "[[true],[false]]");
Assert.assertEquals(2, list.size());
Assert.assertEquals(Arrays.asList(true), list.get(0));
Assert.assertEquals(Arrays.asList(false), list.get(1));
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.qjson.TypeLiteral;
import java.util.*;
import org.junit.Assert;

public class Demo {
    
    public static void demo() {
        {{ CODE }}
    }
}
```

</hide>

# user_defined_container