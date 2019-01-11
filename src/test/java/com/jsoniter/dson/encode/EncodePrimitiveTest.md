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