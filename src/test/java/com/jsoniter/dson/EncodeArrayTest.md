# typed_array

| value | encoded |
| ---   | ---     |
| `new String[]{"hello"}` | `["hello"]` |
| `new String[]{"a","b"}` | `["a","b"]` |
| `new String[]{"a","b","c","d","e"}` | `["a","b","c","d","e"]` |
| `new byte[][]{new byte[]{1,2,3}}` | `["\/AB\/AC\/AD"]` |
| `new boolean[]{true}` | `[true]` |
| `new Boolean[]{true}` | `[true]` |
| `new byte[]{1,2,3}` | `"\/AB\/AC\/AD"` |
| `new Byte[]{1,2,3}` | `"\/AB\/AC\/AD"` |
| `new short[]{1}` | `["\b;;;;;;<"]` |
| `new Short[]{1}` | `["\b;;;;;;<"]` |
| `new int[]{1}` | `["\b;;;;;;<"]` |
| `new Integer[]{1}` | `["\b;;;;;;<"]` |
| `new long[]{100L}` | `["\b;;;;;;;;;;;;>?"]` |
| `new Long[]{100L}` | `["\b;;;;;;;;;;;;>?"]` |
| `new float[]{1.1F}` | `["\f;>ZWGTNAK;;;;;"]` |
| `new Float[]{1.1F}` | `["\f;>ZWGTNAK;;;;;"]` |
| `new double[]{1.1D}` | `["\f;>ZWGTNAGTNAGU"]` |
| `new Double[]{1.1D}` | `["\f;>ZWGTNAGTNAGU"]` |

# temp

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