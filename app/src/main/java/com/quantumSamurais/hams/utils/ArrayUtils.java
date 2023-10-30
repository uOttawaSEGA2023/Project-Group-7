package com.quantumSamurais.hams.utils;

import java.util.ArrayList;

public class ArrayUtils {

    public static ArrayList<Integer> packBytes(byte[] bytes) {
        ArrayList<Integer> packed = new ArrayList<>();
        int toPack = 0;
        int extra = bytes.length%4;
        for (int i = 0; i < bytes.length-extra; i += 4) {
            toPack = bytes[i];
            toPack <<= 8;
            if(bytes[i+1] < 0) {
                toPack += 0x100;
            }
            toPack += bytes[i+1];
            toPack <<= 8;
            if(bytes[i+2] < 0) {
                toPack += 0x100;
            }
            toPack += bytes[i+2];
            toPack <<= 8;
            if(bytes[i+3] < 0) {
                toPack += 0x100;
            }
            toPack += bytes[i+3];
            packed.add(toPack);
            toPack = 0;
        }
        packed.add(0);
        if(extra > 0) {
            for (int i = bytes.length-extra; i < bytes.length; i++) {
                toPack = toPack << 8;
                if(bytes[i] < 0) {
                    toPack += 0x100;
                }
                toPack += bytes[i];
            }
            packed.add(toPack);
        }
        return packed;
    }

    public static byte[] unpackBytes(ArrayList<Integer> list) {
        int lastInt = list.get(list.size()-1);
        byte unpack = 0;
        byte[] unpacked;
        int extra = 0;
        if(lastInt == 0) {
            unpacked = new byte[(list.size()-1)*4];
        } else {
            int temp = lastInt;
            while(temp > 0) {
                extra += 1;
                temp >>= 8;
            }
            unpacked = new byte[(list.size()-2)*4+extra];
        }
        int k = 0;
        int limit = list.size()-1;
        if(extra > 0) {
            limit -= 1;
        }
        for (int j = 0; j < limit; j++) {
            unpack = (byte) (list.get(j) >> 24 & 0xFF);
            unpacked[k] = unpack;
            unpack = (byte) (list.get(j) >> 16 & 0xFF);
            unpacked[k+1] = unpack;
            unpack = (byte) (list.get(j) >> 8 & 0xFF);
            unpacked[k+2] = unpack;
            unpack = (byte) (list.get(j) & 0xFF);
            unpacked[k+3] = unpack;
            k += 4;
        }
        if(extra > 0) {
            int z = 0;
            while(z < extra) {
                unpack = (byte) (lastInt >> 8*(extra-z-1) & 0xFF);
                unpacked[k] = unpack;
                k++;
                z++;
            }
        }
        return unpacked;
    }
}
