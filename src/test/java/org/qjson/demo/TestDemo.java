package org.qjson.demo;

import org.qjson.junit.md.CompileClasses;
import org.qjson.junit.md.LoadClass;
import org.qjson.junit.md.Rmtree;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.qjson.test.md.TestInMarkdown.myTestData;

public interface TestDemo {

    static void $() {
        testDemos();
    }

    static void testDemos() {
        List<String> codes = myTestData().codes();
        int i = 0;
        for (; i < codes.size(); i++) {
            String code = codes.get(i);
            if (!code.startsWith("package")) {
                break;
            }
        }
        List<String> demoCodes = codes.subList(i, codes.size() - 1);
        String template = codes.get(codes.size() - 1);
        for (String demoCode : demoCodes) {
            ArrayList<String> sources = new ArrayList<>(codes.subList(0, i));
            sources.add(template.replace("{{ CODE }}", demoCode));
            try {
                testDemo(sources);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void testDemo(List<String> sources) throws Exception {
        Path path = CompileClasses.$(sources);
        try {
            Class demoClass = LoadClass.$(path, "demo.Demo");
            Method demoMethod = demoClass.getMethod("demo");
            demoMethod.invoke(null);
        } finally {
            Rmtree.$(path);
        }
    }
}
