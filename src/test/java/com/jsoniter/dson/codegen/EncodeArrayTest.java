package com.jsoniter.dson.codegen;

import com.jsoniter.dson.encode.BytesBuilder;
import com.jsoniter.dson.encode.BytesStream;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mdkt.compiler.SourceCode;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class EncodeArrayTest {

    @Test
    public void null_array() {
        BytesStream stream = new BytesStream();
        stream.encodeObject(null);
        Assert.assertEquals("null", stream.toString());
    }

    @Test
    public void one_element() {
        BytesStream stream = newStream();
        stream.encodeObject(new Object[]{"hello"});
        Assert.assertEquals("null", stream.toString());
    }

    @Test
    public void javac() throws Exception {
        String program = "" +
                "package gen;" +
                "public class CodeGenTest {\n" +
                "  public static void main(String[] args) {\n" +
                "    System.out.println(\"Hello World, from a generated program!\");\n" +
                "  }\n" +
                "}\n";

        Path dir = CompileClasses.$(Arrays.asList(program));
        System.out.println(dir);
        Rmtree.$(dir);
//
//        CompiledClassLoader classLoader =
//                new CompiledClassLoader(fileManager.getGeneratedOutputFiles());
//
//        Class<?> codeGenTest = classLoader.loadClass("CodeGenTest");
//        Method main = codeGenTest.getMethod("main", String[].class);
//        main.invoke(null, new Object[]{null});
    }

    @NotNull
    private BytesStream newStream() {
        return new BytesStream(clazz -> new Codegen().generateEncoder(clazz), new BytesBuilder());
    }
}
