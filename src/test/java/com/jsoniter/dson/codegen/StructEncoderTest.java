package com.jsoniter.dson.codegen;

import com.jsoniter.dson.spi.StructDescriptor;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class StructEncoderTest {

    public static class MyClass {
        public String field;
        public String getField() {
            return null;
        }
    }

    @Test
    public void fields_over_getters() {
        List<StructDescriptor.Prop> props = StructEncoder.getProperties(new Codegen.Config(), MyClass.class);
        Assert.assertEquals(1, props.size());
        Assert.assertNotNull(props.get(0).field);
    }
}
