# get_field_and_set_field

```java
package testdata;
public class MyClass {

    public String field;

    public MyClass init(String field) {
        this.field = field;
        return this;
    }
}
```

| value | encoded |
| ---   | ---     |
| `new MyClass().init("hello")` | `{"field":"hello"}` |

# getter_and_setter

```java
package testdata;
public class MyClass {

    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public MyClass init(String field) {
        this.field = field;
        return this;
    }
}
```

| value | encoded |
| ---   | ---     |
| `new MyClass().init("hello")` | `{"field":"hello"}` |

# fluent_getter_and_fluent_setter

```java
package testdata;
public class MyClass {

    private String field;

    public String field() {
        return field;
    }

    public void field(String field) {
        this.field = field;
    }

    public MyClass init(String field) {
        this.field = field;
        return this;
    }
}
```

| value | encoded |
| ---   | ---     |
| `new MyClass().init("hello")` | `{"field":"hello"}` |