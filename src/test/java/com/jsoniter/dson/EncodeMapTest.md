# type_map

| type | value | encoded |
| ---  | ---   | ---     |
| `AnyMap<String,String>` | `null` | `null` |
| `AnyMap<String,String>` | `new AnyMap("a","b")` | `{"a":"b"}` |
| `AnyMap<String,String>` |`new AnyMap(null,"b")` | `{"null":"b"}` |