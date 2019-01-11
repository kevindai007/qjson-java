# type_long

| val  | encoded             | comment |
| ---- | ------------------- | ------- |
| 31  | `"\bZ"`            |         |
| 256  | `"\b;C"`            |         |
| 1984 | `"\b;Y<"`           |         |
| 0    | `"\b;"`             |         |
| 1    | `"\b<"`             |         |
| -1   | `"\bZZZZZZZZZZZZJ"` |         |
| -2   | `"\bYZZZZZZZZZZZJ"` |         |

# type_double

| val  | encoded             | comment |
| ---- | ------------------- | ------- |
| 0.0  | `"\f;"`               |         |
| 1.0  | `"\f;;;;;;;;;;WZ>"` |         |
| 1.1  | `"\fUGANTGANTGWZ>"` |         |

# type_string

| val  | encoded             | comment |
| ---- | ------------------- | ------- |
| `hello`  | `"hello"`               |         |
| `中文`  | `"中文"` |         |
| `𐐷` | `"𐐷"` | |
| `𤭢` | `"𤭢"` | |
| `🙏` | `"🙏"` | |
| `"` | `"\/CC"` | |
| `\` | `"\/FM"` | |
| `/` | `"\/CP"` | |
| 0x00 | `"\/AA"` | |
| `h"e"l"l"o` | `"h\/CCe\/CCl\/CCl\/CCo"` | |