package com.jsoniter.dson.codegen;

import com.jsoniter.dson.spi.DsonProperty;
import org.junit.Assert;
import org.junit.Test;

import java.beans.Transient;

public class StructDescriptorTest {

    public static class MyClass {

        public transient String field1;

        @DsonProperty
        public transient String field2;

        @Transient
        public String getField3() {
            return null;
        }

        @Transient
        @DsonProperty
        public String getField4() {
            return null;
        }
    }

    @Test
    public void ignore_transient() {
        StructDescriptor struct = new StructDescriptor(MyClass.class);
        StructProperty field1 = struct.fields.get("field1");
        Assert.assertTrue(field1.getAnnotation(DsonProperty.class).ignore());
        StructProperty field2 = struct.fields.get("field2");
        Assert.assertFalse(field2.getAnnotation(DsonProperty.class).ignore());
        StructProperty field3 = struct.methods.get("getField3");
        Assert.assertTrue(field3.getAnnotation(DsonProperty.class).ignore());
        StructProperty field4 = struct.methods.get("getField4");
        Assert.assertFalse(field4.getAnnotation(DsonProperty.class).ignore());
    }
}
