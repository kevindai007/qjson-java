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

If you container is inherited from containers in `java.util.*`, it is still considered as container automatically.
However, if your container just implements `Iterable`, it will not be encoded as `[]` automatically.
Given this class

<<< @/docs/demo/MyObjects.java


It will be encoded like this:

```java
Assert.assertEquals(
        "{\"obj1\":\"a\",\"obj2\":\"b\"}", // {"obj1":"a","obj2":"b"}
        QJSON.stringify(new MyObjects("a", "b")));
```

To encode it as `["a","b"]`, we need to register a function to choose encoder.
Same applies to decoding.

```java
QJSON.Config cfg = new QJSON.Config();
cfg.chooseEncoder = (qjson, clazz) -> {
    if (MyObjects.class.equals(clazz)) {
        return qjson.encoderOf(Iterable.class);
    }
    return null;
};
QJSON qjson = new QJSON(cfg);
Assert.assertEquals(
        "[\"a\",\"b\"]", // ["a","b"]
        qjson.encode(new MyObjects("a", "b")));
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.demo.MyObjects;

public class Demo {
    
    public static void demo() {
        {{ CODE }}
    }
}
```

</hide>