# Struct

User defined class can be encoded decoded automatically.

# public_fields

Given this class, all the public fields can be get/set directly

<<< @/docs/demo/struct/public_fields/UserPost.java

```java
UserPost post = new UserPost();
post.title = "experience report";
post.content = "that is awsome";
QJSON qjson = new QJSON();
String encoded = qjson.encode(post);
print(encoded);
post = qjson.decode(UserPost.class, encoded);
print(post.title);
// Output:
// {"content":"that is awsome","title":"experience report"}
// experience report
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.demo.struct.public_fields.UserPost;

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

# getter_setter

If getter or setter present, will get/set through it.

<<< @/docs/demo/struct/getter_setter/UserPost.java

```java
UserPost post = new UserPost();
post.setTitle("experience report");
post.setContent("that is awsome");
QJSON qjson = new QJSON();
String encoded = qjson.encode(post);
print(encoded);
post = qjson.decode(UserPost.class, encoded);
print(post.getTitle());
// Output:
// {"content":"that is awsome","title":"experience report"}
// experience report
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.demo.struct.getter_setter.UserPost;

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

# adhoc_rename

To ad-hoc rename some property, we can use `@QJsonProperty`.

<<< @/docs/demo/struct/adhoc_rename/UserPost.java

```java
UserPost post = new UserPost();
post.title = "experience report";
post.content = "that is awsome";
QJSON qjson = new QJSON();
String encoded = qjson.encode(post);
print(encoded);
post = qjson.decode(UserPost.class, encoded);
print(post.title);
// Output:
// {"TITLE":"experience report","content":"that is awsome"}
// experience report
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.demo.struct.adhoc_rename.UserPost;

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

# batch_rename

If you do not want to annotate `@QJsonProperty` one by one.
You can use config to pass in a `customizeStruct` function.

<<< @/docs/demo/struct/public_fields/UserPost.java

```java
UserPost post = new UserPost();
post.title = "experience report";
post.content = "that is awsome";
QJSON.Config cfg = new QJSON.Config();
cfg.customizeStruct = (spi, structDescriptor) -> {
    if (!UserPost.class.equals(structDescriptor.clazz)) {
        return;
    }
    for (StructDescriptor.Prop field : structDescriptor.fields.values()) {
        field.name = field.field.getName().toUpperCase();
    }
};
QJSON qjson = new QJSON(cfg);
String encoded = qjson.encode(post);
print(encoded);
post = qjson.decode(UserPost.class, encoded);
print(post.title);
// Output:
// {"CONTENT":"that is awsome","TITLE":"experience report"}
// experience report
```

<hide>

```java
package demo;
import org.qjson.QJSON;
import org.junit.Assert;
import org.qjson.spi.StructDescriptor;
import org.qjson.demo.struct.public_fields.UserPost;

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