

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BitArray2D {

    List<BitArray> arr;
    int Row;
    int Column;

    public BitArray2D(int x, int y) {
        Row =x ;
        Column = (y/64)+1;

        arr = new ArrayList<>();
        for (int i=0; i<x;i++) {
            arr.add(new BitArray(y));
        }
    }

    public BitArray2D(int row) {
        arr = new ArrayList<>();
        Row=row;
    }

    public void AddNewRow(long [] ele) {

        arr.add(new BitArray(ele));
    }

    public boolean getValue(int x, int y) {
        return arr.get(x).getBit(y);
    }

    public BitArray getValue(int x) {
        return arr.get(x);
    }

    public void setValue(int x, int y, boolean val) {
        arr.get(x).setBit(y, val);
    }


    public void save2DArrayToFile(String path) {

        try {
            saveShape(path);
            File file = new File(path+".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file),65000000);
            System.out.print("Saving bloom filter bit sliced array to bloom_filter.txt took ");
            write(arr, writer);
        }
        catch (IOException ex) {
            System.out.println(ex);
        }

    }

    private void saveShape(String path) throws IOException {
        File file = new File(path + "_shape.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        System.out.println(String.format("shape of the bit Sliced array %s %s" ,Row,Column));
        writer.write(String.format("%s %s" ,(Row)+1,Column));
        writer.close();

    }

    public long[] getSize() {
        var arrSize = new long[2];
        arrSize[0] = arr.size();
        arrSize[1] = arr.get(0).getLength();
        return arrSize;
    }

    public long[] getActualSize() {
        var arrSize = new long[2];
        arrSize[0] = arr.size();
        arrSize[1] = arr.get(0).bits.length;
        return arrSize;
    }

    private static void write(List<BitArray> records, Writer writer) throws IOException {
        long start = System.currentTimeMillis();
        for (BitArray record: records) {
            //System.out.println(record.toString());
            writer.write(record.toString());
            writer.write("\n");
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000f + " seconds");
    }

    private static void writeBuffered(List<BitArray> records, int bufSize) throws IOException {
        File file = File.createTempFile("foo", ".txt");
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer, bufSize);

            System.out.print("Writing buffered (buffer size: " + bufSize + ")... ");
            write(records, bufferedWriter);
        } finally {
            // comment this out if you want to inspect the files afterward
            file.delete();
        }
    }
}

