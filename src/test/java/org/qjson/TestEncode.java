package org.qjson;

import org.qjson.junit.md.*;
import org.junit.Assert;
import org.mdkt.compiler.InMemoryJavaCompiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.qjson.junit.md.TestInMarkdown.stripQuote;
import static org.qjson.junit.md.TestInMarkdown.myTestData;

public interface TestEncode {
    static void $() {
        TestData testData = myTestData();
        Table table = testData.table();
        boolean hasType = "type".equals(table.head.get(0));
        for (Row row : table.body) {
            List<String> sources = new ArrayList<>(testData.codes());
            String source = "" +
                    "package testdata;\n" +
                    "import java.util.*;\n" +
                    "import org.qjson.any.*;\n" +
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
                QJSON.Config config = new QJSON.Config();
                config.compiler = InMemoryJavaCompiler.newInstance()
                        .ignoreWarnings()
                        .useParentClassLoader(clazz.getClassLoader())
                        .useOptions("-classpath", System.getProperty("java.class.path") + ":" + tempDir.toString());
                QJSON qjson = new QJSON(config);
                Assert.assertEquals(stripQuote(row.get(hasType ? 2 : 1)), qjson.encode(testObject));
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
