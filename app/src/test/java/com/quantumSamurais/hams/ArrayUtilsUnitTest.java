package com.quantumSamurais.hams;

import org.junit.Test;
import static org.junit.Assert.*;

import com.quantumSamurais.hams.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayUtilsUnitTest {

    @Test
    public void testPackBytes() {
        byte[] toPack = new byte[] { 10, -20, 30};
        ArrayList<Integer> packed = new ArrayList<>();
        packed.add(0);
        packed.add(0x0AEC1E);
        assertEquals(packed, ArrayUtils.packBytes(toPack));
        packed.clear();
        toPack = new byte[] {10,-20,30,22,55,60,80,-20};
        packed.add(0x0AEC1E16);
        packed.add(0x373C50EC);
        packed.add(0);
        assertEquals(packed,ArrayUtils.packBytes(toPack));
    }

    @Test
    public void testUnpackBytes() {
        byte[] unPacked = new byte[] { 10, -20, 30};
        ArrayList<Integer> packed = new ArrayList<>();
        packed.add(0);
        packed.add(0x0AEC1E);
        assertArrayEquals(unPacked, ArrayUtils.unpackBytes(packed));
    }

}
