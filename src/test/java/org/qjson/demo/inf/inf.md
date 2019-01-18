# Interface

Interface type need some special setup.

# encode

Normally, we do not need to consider interface when encode. 
Because we can always use `obj.getClass()` to get the conrete implementation class.
However, if the object is created from a private class, the encoder can not reference the private class.
In this case, we need to choose the type to encode.

<<< @/docs/demo/inf/My.java

```java
Object obj = My.newObject();
QJSON qjson = new QJSON();
try {
    print(qjson.encode(obj));
} catch(QJsonEncodeException e) {
    print(e.getMessage());
}
// Output:
// class org.qjson.demo.inf.My$PrivateClass is private, need to use config to specify encoder manually
```

To use the public interface `My.Inf` to encode, we need to choose encoder.

```java
Object obj = My.newObject();
QJSON.Config cfg = new QJSON.Config();
cfg.chooseEncoder = (qjson, clazz) -> {
    if (!My.Inf.class.isAssignableFrom(clazz)) {
        return null;
    }
    if (My.Inf.class.equals(clazz)) {
        return null;
    }
    return qjson.encoderOf(My.Inf.class);
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