package org.qjson.encode;

public interface Append {

    static void $(StringBuilder builder, char c1, char c2, char c3, char c4, char c5, char c6, char c7, char c8,
                  char c9, char c10, char c11) {
        builder.append(new char[]{c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11});
    }

    static void $(StringBuilder builder, char c1, char c2, char c3, char c4, char c5, char c6, char c7, char c8,
                  char c9, char c10, char c11, char c12, char c13, char c14, char c15, char c16, char c17, char c18) {
        builder.append(new char[]{c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18});
    }
}
