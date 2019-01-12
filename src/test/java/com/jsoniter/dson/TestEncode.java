package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.nio.file.Path;
import java.util.Arrays;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public interface TestEncode {
    static void $() {
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
                Object testObject = clazz.getMethod("create").invoke(null);
                DSON.Config config = new DSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
                DSON dson = new DSON(config);
                Assert.assertEquals(stripQuote(row.get(1)), dson.encode(testObject));
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
