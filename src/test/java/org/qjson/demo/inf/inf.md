# Interface

Interface type need some special setup.

# encode

Normally, we do not need to consider interface when encode. 
Because we can always use `obj.getClass()` to get the concrete implementation class.
However, if the object is created from a private class, the encoder can not reference the private class.
In this case, we need to choose the type to encode.

<<< @/docs/demo/inf/My.java

```java
Object obj = My.newObject();
QJSON qjson = new QJSON();
try {
    print(qjson.encode(obj));
} catch(Exception e) {
    print("exception thrown");
}
// Output:
// exception thrown
```

To use the public interface `My.Inf` to encode, we need to choose encoder.

```java
Object obj = My.newObject();
QJSON.Config cfg = new QJSON.Config();
cfg.chooseEncoder = (qjson, clazz) -> {
    if (QJSON.isSubType(My.Inf.class, clazz)) {
        return qjson.encoderOf(My.Inf.class);
    }
    return null;
};
QJSON qjson = new QJSON(cfg);
print(qjson.encode(obj));
// Output:
// {"field":"hello"}
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.demo.inf.My;
import org.qjson.encode.QJsonEncodeException;

public class Demo {
    
    public static void demo() {
        {{ CODE }}
    }
    
    private static void print(Object obj) {
        System.out.println(obj);
    }
}
```

</hide>

# decode

Decode into a interface type always need to choose the decoder to use, 
unless the interface is a well-known builtin collection type.

For example, `List.class`:

```java
QJSON qjson = new QJSON();
List list = qjson.decode(List.class, "[]");
Assert.assertEquals(0, list.size());
```

For other interface, if no decoder specified, exception will be thrown:

<<< @/docs/demo/inf/My.java

```java
QJSON qjson = new QJSON();
try {
    print(qjson.decode(My.Inf.class, "{\"field\":\"hello\"}"));
} catch(Exception e) {
    print("exception thrown");
}
// Output:
// exception thrown
```

Choose the decoder need to pass in a config texception throwno qjson:

```java
QJSON.Config cfg = new QJSON.Config();
cfg.chooseDecoder = (qjson, type) -> {
    if (My.Inf.class.equals(type)) {
        return qjson.decoderOf(My.PublicClass.class);
    }
    return null;
};
QJSON qjson = new QJSON(cfg);
My.Inf obj = qjson.decode(My.Inf.class, "{\"field\":\"hello\"}");
print(obj.getField());
// Output:
// hello
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import java.util.*;
import org.junit.Assert;
import org.qjson.demo.inf.My;

public class Demo {
    
    public static void demo() {
        {{ CODE }}
    }
    
    private static void print(Object obj) {
        System.out.println(obj);
    }
}
```

</hide>