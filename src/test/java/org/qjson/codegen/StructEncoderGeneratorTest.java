package org.qjson.codegen;

import org.qjson.spi.StructDescriptor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StructEncoderGeneratorTest {

    public static class MyClass {
        public String field;
        public String getField() {
            return null;
        }
    }

    @Test
    public void fields_over_getters() {
        List<StructDescriptor.Prop> props = StructEncoderGenerator.getProperties(
                new Codegen.Config(), null, MyClass.class);
        Assert.assertEquals(1, props.size());
        Assert.assertNotNull(props.get(0).field);
    }
}
