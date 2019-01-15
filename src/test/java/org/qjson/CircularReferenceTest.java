package org.qjson;

import org.junit.Test;

public class CircularReferenceTest {

    @Test
    public void ref_map_value() {
        TestEncode.$();
        TestDecode.$();
    }

    @Test
    public void ref_list_value() {
        TestEncode.$();
    }

    @Test
    public void ref_struct_field() {
        TestEncode.$();
    }

    @Test
    public void ref_itself() {
        TestEncode.$();
    }
}
