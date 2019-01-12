# simple_elements

| value | encoded |
| ---   | ---     |
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