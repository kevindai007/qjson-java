package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import org.junit.Assert;
import org.junit.Test;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodeArrayTest {

    @Test
    public void object_array() {
        testDecode(Object[].class);
    }

    @Test
    public void string_array() {
        testDecode(String[].class);
    }

    private void testDecode(Type type) {
        FluentAPI testData = testDataFromMySection();
        for (Row row : testData.table().body) {
            String source = "" +
                    "package testdata;\n" +
                    "import java.util.*;\n" +
                    "public class TestObject {\n" +
                    "   public static Object create() {\n" +
                    "       return " + stripQuote(row.get(0)) + ";\n" +
                    "   }\n" +
                    "}";
            Path tempDir = CompileClasses.$(Arrays.asList(source));
            try {
                Class testDataClass = LoadClass.$(tempDir, "testdata.TestObject");
                Object testObject = testDataClass.getMethod("create").invoke(null);
                DSON.Config config = new DSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
                DSON dson = new DSON(config);
                byte[] bytes = stripQuote(row.get(1)).getBytes(StandardCharsets.UTF_8);
                Object decoded = dson.decode(type, bytes, 0, bytes.length);
                Assert.assertTrue(Objects.deepEquals(testObject, decoded));
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                Rmtree.$(tempDir);
            }
        }
    }
}
