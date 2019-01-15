package org.qjson;

import org.junit.Test;

public class ReferenceTest {

    @Test
    public void ref_map_value() {
        TestEncode.$();
        TestDecode.$();
    }

    @Test
    public void ref_list_element() {
        TestEncode.$();
        TestDecode.$();
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
