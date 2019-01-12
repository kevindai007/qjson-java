package com.jsoniter.dson;

import com.dexscript.test.framework.FluentAPI;
import com.dexscript.test.framework.Row;
import com.jsoniter.dson.codegen.Codegen;
import com.jsoniter.dson.encode.BytesEncoderSink;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.Arrays;

import static com.dexscript.test.framework.TestFramework.stripQuote;
import static com.dexscript.test.framework.TestFramework.testDataFromMySection;

public class EncodeArrayTest {

    @Test
    public void null_array() {
        BytesEncoderSink stream = new BytesEncoderSink();
        stream.encodeObject(null);
        Assert.assertEquals("null", stream.toString());
    }

    @Test
    public void one_element() {
        FluentAPI testData = testDataFromMySection();
        for (Row row : testData.table().body) {
            String source = "" +
                    "package testdata;\n" +
                    "public class TestObject {\n" +
                    "   public static Object create() {\n" +
                    "       return " + stripQuote(row.get(0)) + ";\n" +
                    "   }\n" +
                    "}";
            Path tempDir = CompileClasses.$(Arrays.asList(source));
            try {
                Class clazz = LoadClass.$(tempDir, "testdata.TestObject");
                Object testObject = clazz.getMethod("create").invoke(null);
                Codegen codegen = new Codegen();
                DSON dson = new DSON(codegen);
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
