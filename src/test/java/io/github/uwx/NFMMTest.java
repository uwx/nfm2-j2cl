package io.github.uwx;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@J2clTestInput(NFMMTest.class)
public class NFMMTest {

    @Test
    public void someSimpleTest() {
        assertEquals(NFMM.HELLO_WORLD, new NFMM().helloWorldString());
    }
}
