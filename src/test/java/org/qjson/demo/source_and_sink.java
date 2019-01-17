package org.qjson.demo;

import org.junit.Test;
import org.qjson.junit.md.CompileClasses;
import org.qjson.junit.md.LoadClass;
import org.qjson.junit.md.Rmtree;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.qjson.test.md.TestInMarkdown.myTestData;

public class source_and_sink {

    @Test
    public void decode_source() {
        testDemos();
    }

    @Test
    public void encode_sink() {
        testDemos();
    }

    private void testDemos() {
        List<String> codes = myTestData().codes();
        List<String> demoCodes = codes.subList(0, codes.size() - 1);
        String template = codes.get(codes.size() - 1);
        for (String demoCode : demoCodes) {
            try {
                testDemo(template, demoCode);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void testDemo(String template, String demoCode) throws Exception {
        demoCode = template.replace("{{ CODE }}", demoCode);
        Path path = CompileClasses.$(Arrays.asList(demoCode));
        try {
            Class demoClass = LoadClass.$(path, "demo.Demo");
            Method demoMethod = demoClass.getMethod("demo");
            demoMethod.invoke(null);
        } finally {
            Rmtree.$(path);
        }
    }
}
