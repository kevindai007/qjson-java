# object_array

| value | encoded |
| ---   | ---     |
| `null` | `null` |
| `new Object[0]` | `[]` |
| `new Object[1]` | `[null]` |
| `new Object[]{"hello"}` | `["hello"]` |
| `new Object[]{"hello","world"}` | `["hello","world"]` |
| `new Object[]{(byte)100}` | `["\b;;;;;>?"]` |
| `new Object[]{(short)100}` | `["\b;;;;;>?"]` |
| `new Object[]{100}` | `["\b;;;;;>?"]` |
| `new Object[]{100L}` | `["\b;;;;;;;;;;;;>?"]` |
| `new Object[]{'a'}` | `["a"]` |
| `new Object[]{true}` | `[true]` |
| `new Object[]{1.1F}` | `["\f;>ZWGTNAK;;;;;"]` |
| `new Object[]{1.1D}` | `["\f;>ZWGTNAGTNAGU"]` |
| `new Object[]{new byte[]{1,2,3}}` | `["\/AB\/AC\/AD"]` |

# string_array

| value | encoded |
| ---   | ---     |
| `null` | `null` |
| `new String[]{"hello"}` | `["hello"]` |
| `new String[]{"a","b"}` | `["a","b"]` |
| `new String[]{"a","b","c","d","e"}` | `["a","b","c","d","e"]` |

# primitive_array

| value | encoded |
| ---   | ---     |
| `new int[]{1}` | `["\b;;;;;;<"]` |
| `new byte[]{1}` | `"\/AB"` |
| `new long[]{1}` | `["\b;;;;;;;;;;;;;<"]` |