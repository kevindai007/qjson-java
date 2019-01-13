package org.qjson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import com.dexscript.test.framework.Table;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public interface TestDecode {

    static void $() {
        FluentAPI testData = testDataFromMySection();
        Table table = testData.table();
        boolean hasType = "type".equals(table.head.get(0));
        for (Row row : table.body) {
            List<String> sources = new ArrayList<>(testData.codes());
            String typeLiteral = hasType ? "new TypeLiteral<" + stripQuote(row.get(0)) + ">(){}" : "null";
            String source = "" +
                    "package testdata;\n" +
                    "import org.qjson.any.*;\n" +
                    "import org.qjson.*;\n" +
                    "import java.util.*;\n" +
                    "public class TestObject {\n" +
                    "   public static TypeLiteral type() {\n" +
                    "       return " + typeLiteral + ";\n" +
                    "   }\n" +
                    "   public static Object create() {\n" +
                    "       return " + stripQuote(row.get(hasType ? 1 : 0)) + ";\n" +
                    "   }\n" +
                    "}";
            sources.add(source);
            Path tempDir = CompileClasses.$(sources);
            try {
                Class testDataClass = LoadClass.$(tempDir, "testdata.TestObject");
                Object testObject = testDataClass.getMethod("create").invoke(null);
                TypeLiteral testObjectType = (TypeLiteral) testDataClass.getMethod("type").invoke(null);
                QJSON.Config config = new QJSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance()
                        .ignoreWarnings()
                        .useParentClassLoader(testDataClass.getClassLoader())
                        .useOptions("-classpath", System.getProperty("java.class.path") + ":" + tempDir.toString());
                QJSON qjson = new QJSON(config);
                byte[] bytes = stripQuote(row.get(hasType ? 2 : 1)).getBytes(StandardCharsets.UTF_8);
                if (hasType) {
                    Object decoded = qjson.decode(testObjectType, bytes, 0, bytes.length);
                    Assert.assertTrue(row.get(hasType ? 1 : 0), Objects.deepEquals(testObject, decoded));
                } else {
                    Object decoded = qjson.decode(testObject.getClass(), bytes, 0, bytes.length);
                    Assert.assertTrue(row.get(hasType ? 1 : 0), Objects.deepEquals(testObject, decoded));
                }
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
