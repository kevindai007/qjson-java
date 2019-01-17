# Get started

Static methods are provided as shortcut

```java
String encoded = QJSON.stringify(your_object);
Any any = QJSON.parse(encoded); // decode as map/list
```

Methods taking more options need to `new QJSON()` first

```java
QJSON qjson = new QJSON();
BytesBuilder bytesBuilder = new BytesBuilder();
qjson.encode(your_object, bytesBuilder); // encode to byte[]
byte[] encoded = bytesBuilder.copyOfBytes();
qjson.decode(your.class, encoded); // decode byte[] to specific class
```

`QJSON` constructor take config object to customize its behavior

```java
QJSON.Config cfg = new QJSON.Config();
QJSON qjson = new QJSON(cfg);
```