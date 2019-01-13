package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import com.dexscript.test.framework.Table;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public interface TestEncode {
    static void $() {
        FluentAPI testData = testDataFromMySection();
        Table table = testData.table();
        boolean hasType = "type".equals(table.head.get(0));
        for (Row row : table.body) {
            List<String> sources = new ArrayList<>(testData.codes());
            String source = "" +
                    "package testdata;\n" +
                    "import java.util.*;\n" +
                    "import com.jsoniter.dson.any.*;\n" +
                    "public class TestObject {\n" +
                    "   public static Object create() {\n" +
                    "       return " + stripQuote(row.get(hasType ? 1 : 0)) + ";\n" +
                    "   }\n" +
                    "}";
            sources.add(source);
            Path tempDir = CompileClasses.$(sources);
            try {
                Class clazz = LoadClass.$(tempDir, "testdata.TestObject");
                Object testObject = clazz.getMethod("create").invoke(null);
                DSON.Config config = new DSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance()
                        .ignoreWarnings()
                        .useParentClassLoader(clazz.getClassLoader())
                        .useOptions("-classpath", System.getProperty("java.class.path") + ":" + tempDir.toString());
                DSON dson = new DSON(config);
                Assert.assertEquals(stripQuote(row.get(hasType ? 2 : 1)), dson.encode(testObject));
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
