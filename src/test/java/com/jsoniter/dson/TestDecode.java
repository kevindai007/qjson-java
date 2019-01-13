package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import com.dexscript.test.framework.Table;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public interface TestDecode {

    static void $() {
        Table table = testDataFromMySection().table();
        boolean hasType = "type".equals(table.head.get(0));
        for (Row row : table.body) {
            String typeLiteral = hasType ? "new TypeLiteral<" + stripQuote(row.get(0)) + ">(){}" : "null";
            String source = "" +
                    "package testdata;\n" +
                    "import com.jsoniter.dson.any.*;\n" +
                    "import com.jsoniter.dson.*;\n" +
                    "import java.util.*;\n" +
                    "public class TestObject {\n" +
                    "   public static TypeLiteral type() {\n" +
                    "       return " + typeLiteral + ";\n" +
                    "   }\n" +
                    "   public static Object create() {\n" +
                    "       return " + stripQuote(row.get(hasType ? 1 : 0)) + ";\n" +
                    "   }\n" +
                    "}";
            Path tempDir = CompileClasses.$(Arrays.asList(source));
            try {
                Class testDataClass = LoadClass.$(tempDir, "testdata.TestObject");
                Object testObject = testDataClass.getMethod("create").invoke(null);
                TypeLiteral testObjectType = (TypeLiteral) testDataClass.getMethod("type").invoke(null);
                DSON.Config config = new DSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance().ignoreWarnings();
                DSON dson = new DSON(config);
                byte[] bytes = stripQuote(row.get(hasType ? 2 : 1)).getBytes(StandardCharsets.UTF_8);
                Object decoded = dson.decode(testObjectType, bytes, 0, bytes.length);
                Assert.assertTrue(row.get(hasType ? 1 : 0), Objects.deepEquals(testObject, decoded));
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
