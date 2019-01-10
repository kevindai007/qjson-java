# java-dson

DSON is a subset of JSON for better performance. Every DSON is a valid JSON.

JSON is slow for following reasons:

* unescape string need many condition instructions
* byte array need to be encoded as base64, as string is only valid for utf-8 range
* integer is 10 based
* double is hard to encode/decode

DSON solve this problem by:

* encode Unicode U+237E (⍾) as "\/23\/7E"
* encode 256 as "\bFF", as "\b" reinterpreted as uint64
* encode 1.1 as "\f3FF199999999999A",as "\f" reinterpreted as float64

Value encoded in DSON, can be decoded as JSON, then the string values need to be decoded again to original value.
Or we can use DSON to decode it directly back.