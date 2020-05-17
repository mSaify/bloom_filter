

import java.util.HashMap;
import java.util.HashSet;

public class BitArray {

    private static final int MASK = 63;
    private final long len;
    public long bits[] = null;

    public BitArray(long size) {
        if ((((size-1)>>6) + 1) > 2147483647) {
            throw new IllegalArgumentException(
                    "Field size to large, max size = 137438953408");
        }else if (size < 1) {
            throw new IllegalArgumentException(
                    "Field size to small, min size = 1");
        }
        len = size;
        bits = new long[(int) (((size-1)>>6) + 1)];
    }

    public BitArray(long size,Boolean initialVal) {
        if ((((size-1)>>6) + 1) > 2147483647) {
            throw new IllegalArgumentException(
                    "Field size to large, max size = 137438953408");
        }else if (size < 1) {
            throw new IllegalArgumentException(
                    "Field size to small, min size = 1");
        }
        len = size;
        var bitSize = (int) (((size-1)>>6) + 1);
        bits = new long[bitSize];

        if(initialVal==true) {
            for(int i=0;i<bitSize;i++) {
                bits[i]= 0xFFFFFFFFFFFFFFFFL;
            }
        }
    }

    public BitArray(long[] ele) {
        len=ele.length;
        bits = ele;
    }

    public boolean getBit(long pos) {
        //System.out.println((bits[(int)(pos>>6)] & (1L << (pos&MASK))));
        return (bits[(int)(pos>>6)] & (1L << (pos&MASK))) != 0;
    }

    public boolean getSingleLongBit(long val,long pos) {
        return (val & (1L << (pos&MASK))) != 0;
    }

    public void setBit(long pos, boolean b) {
        if (getBit(pos) != b) { bits[(int)(pos>>6)] ^= (1L << (pos&MASK)); }
    }

    public BitArray And(BitArray arr) {
        BitArray result = new BitArray(arr.getLength());
        //System.out.println(arr.bits.length);
        for(int i=0; i<this.bits.length; i++) {
            result.bits[i] = this.bits[i] & arr.bits[i];
        }
        return result;
    }

    public BitArray Or(BitArray arr) {
        BitArray result = new BitArray(arr.getLength());
        for(int i=0; i<this.bits.length; i++) {
            result.bits[i] = this.bits[i] | arr.bits[i];
        }
        return result;
    }

    public HashSet<Integer> GetPositionsWhereBitValue(boolean value) {
        var resultSet = new HashSet<Integer>();

   //     long valToCheck=1l;
//        if(value==false) valToCheck=0L;
//
////        for(int i=0; i< bits.length; i++) {
////
////            var val = bits[i];
////            var currPos=63;
////            while(val!=valToCheck) {
////                val = valToCheck>>1;
////                if(getSingleLongBit(val, 63) == true) {
////                    resultSet.add(currPos);
////                    currPos--;
////                }
////            }
////
////        }

        var iter = (bits.length *64)-2;
        for(int i=0; i<iter; i++) {
            if(getBit(i)==true) {
                resultSet.add(i);
            }
        }
        return resultSet;
    }

    @Override
    public String toString() {
        var str="";
        for (long val: bits) {
            if(str=="") {
                str = str+val;
            }
            else
                str =  str + " " + val;
        }
        return  str.trim();
    }

    public long getLength() {
        return len;
    }
}