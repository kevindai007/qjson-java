# type_map

| value | encoded |
| ---   | ---     |
| `null` | `null` |
| `new LinkedHashMap(){{ put("a","b");}}` | `{"a":"b"}` |
| `new LinkedHashMap(){{ put(null,"b");}}` | `{"null":"b"}` |