# java-dson

DSON is a subset of JSON for better performance. Every DSON is a valid JSON.

JSON is slow for following reasons:

* unescape string need many condition instructions
* byte array need to be encoded as base64, as string is only valid for unicode character
* integer is 10 based
* double is hard to encode/decode

DSON solve this problem by:

* encode control character 0x00 the "A" within `"\/AA"` represents 0. `\/` is the only escape form expected in string to escape single byte.
* `\"` will never appear, so we can skip string without considering escape characters
* encode 256 as "\b;;;;;;;;;;;;C;", as `"\b"` reinterpreted as integer
* encode 1.1 as "\f;>ZWGTNAGTNAGU",as `"\f"` reinterpreted as float
* do not support whitespace

Value encoded in DSON, can be decoded as JSON, then the string values need to be decoded again to original value.
Or we can use DSON to decode it directly back.

TODO

* encode to and decode from java string without intermediate `byte[]`
* support referencing encountered object using `"\\'key1'.0.'key2'"` notation