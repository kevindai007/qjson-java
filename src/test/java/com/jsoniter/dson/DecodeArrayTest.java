package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import com.jsoniter.dson.codegen.Codegen;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class DecodeArrayTest {

    @Test
    public void object_array() {
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
                Class clazz = LoadClass.$(tempDir, "testdata.TestObject");
                Object[] testObject = (Object[]) clazz.getMethod("create").invoke(null);
                Codegen codegen = new Codegen();
                DSON.Config config = new DSON.Config();
                config.codegen = codegen;
                DSON dson = new DSON(config);
                Assert.assertArrayEquals(testObject, dson.decode(Object[].class, stripQuote(row.get(1))));
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
