# customize_encoder

```java
package testdata;
import com.jsoniter.dson.spi.*;
public class StringAsBoolean implements Encoder {
    public void encode(EncoderSink sink, Object val) {
        boolean b = Boolean.valueOf((String)val);
        sink.encodeBoolean(b);
    }
}
```

```java
package testdata;
import com.jsoniter.dson.spi.*;
public class MyClass {

    @DsonProperty(encoder = StringAsBoolean.class)
    public String field;

    public MyClass init(String field) {
        this.field = field;
        return this;
    }
}
```

| value | encoded |
| ---   | ---     |
| `new MyClass().init("true")` | `{"field":true}` |