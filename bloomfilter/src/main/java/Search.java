

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Search implements Operation, ReadFileOperation {

    BitArray2D bloomFilterArray;
    private int bloomFilterRows;
    private int bloomFilterColumns;
    BitArray2D termArray;
    private String path;
    private String queryPath;
    private String termDictPath;
    int[] randomSeeds;
    private byte maxNoHashFunctions;

    Search(String queryPa) {
        bloomFilterArray = new BitArray2D(bloomFilterRows);
        path="bloomFilter";
        this.queryPath=queryPa;
        termDictPath = "term_hash_lookup.txt";
        randomSeeds = new int[] { 1234, 4321, 6789, 9876, 3456, 6543 };
        maxNoHashFunctions = 3;
    }

    private void SetBloomFilterDimensions()  {
        try {
            File file = new File(path + "_shape.txt");
            FileReader fileReader = new FileReader(file, Charset.defaultCharset());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var arr = line.split(" ");
                bloomFilterRows=Integer.parseInt(arr[0]);
                bloomFilterColumns=Integer.parseInt(arr[1]);
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private List<String> readQueryFile() {

        List<String> queries = new ArrayList<>();
        try {
            File file = new File(this.queryPath);
            FileReader fileReader = new FileReader(file,  Charset.defaultCharset());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var arr = Arrays.asList(StemmerWrapper.getStemmedWords(line.toLowerCase()).split(" "));
                queries.addAll(arr);
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return queries;
    }

    @Override
    public void execute() {
        var queries = readQueryFile();
        SetBloomFilterDimensions();
        GenerateBitArrayFromFile(path + ".txt");


        var termHashFunctionDic = GetTermHashDict(queries);

        var result = findDocumentsForQueries(queries, termHashFunctionDic);

        System.out.println("The result for the terms found and their document id " );
        var counter=0;
        for(Map.Entry<String, Set<Integer>> res : result.entrySet()) {
            if (counter==100)
                break;
            else
                counter++;
            System.out.println(String.format("%s [%s]",res.getKey(), GetStringFromSet(res.getValue()) ));
            System.out.println(String.format("%s [%s]",res.getKey(), GetStringFromSet(res.getValue()) ));
        }

        new ReportGeneration(result).execute();

    }

    private HashMap<String, Set<Integer>> findDocumentsForQueries(List<String> queries,HashMap<String, Byte> termHashDic) {

        var hashFunctions =  InitializeHashFunctions((byte)maxNoHashFunctions);
        var queryIndex=0;
        var finalResult = new HashMap<String, Set<Integer>>();
        long totalTime = 0L;
        long finalStart = System.currentTimeMillis();
        for(String query: queries) {

            long start = System.currentTimeMillis();

            BitArray result=null;

            var noOfHashFunctions= maxNoHashFunctions; //need to fix this
            BitArray termBitRow = null;

            if(termHashDic.size()>0) {
                for (int i = 0; i < noOfHashFunctions; i++) {

                    var hashVal = hashFunctions.get(i).getHashValue(query);
                    var arrSizeOfTermsBit = (int)bloomFilterArray.getSize()[0];

                    var termRow = Math.abs((int) hashVal % (arrSizeOfTermsBit));
                    var bloomFilterTermRow = bloomFilterArray.getValue(termRow);


                    if (i == 0) {
                        termBitRow = new BitArray(arrSizeOfTermsBit, true);
                        termBitRow = bloomFilterTermRow.And(termBitRow);
                        //System.out.println(termRow);
                    } else {
                        result = bloomFilterTermRow.And(termBitRow);
                        //System.out.println(result);
                    }


                }
                if (result != null && !finalResult.containsKey(query)) {
                    var bitPos = result.GetPositionsWhereBitValue(true);
                    finalResult.put(query, bitPos);
                }
            }

            long end = System.currentTimeMillis();
            totalTime =  totalTime + ((end-start));
        }

        long finalend = System.currentTimeMillis();
        System.out.println("Total Search Query for all queries " + (finalend-finalStart)/1000f + " in secs" );
        System.out.println("Average Query Time for each " + totalTime*1.0f/queries.size() + "  in mili secs" );
        return finalResult;
    }

    private HashMap<String, Byte> GetTermHashDict(List<String> queries) {
        var termHashDict = new HashMap<String, Byte>();

        try {
            File file = new File(termDictPath);
            FileReader fileReader = new FileReader(file, Charset.defaultCharset());
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var arr =line.split("\\|");
                var term = arr[0];
                var hashFunc = arr[1];
                //System.out.println("hash func and term " + term + " " + hashFunc);
                if(queries.contains(term) && !termHashDict.containsKey(term)) {
                    termHashDict.put(term,  Byte.parseByte(hashFunc.trim()));
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return termHashDict;

    }

    private List<HashWrapper> InitializeHashFunctions(byte maxHashFunctions) {

        List<HashWrapper> hashFunctions = new ArrayList<>();

        for (int i=0; i<maxHashFunctions; i++) {
            hashFunctions.add(new HashWrapper(randomSeeds[i]));
        }

        return hashFunctions;
    }


    public void GenerateBitArrayFromFile(String path) {

        ReadFileOperation fileProcessing = this;
        System.out.println(this);
        try {
            FileOperations fileOperations = new FileOperations();
            fileOperations.readFileAndPerformOperation(path, fileProcessing);
        }
        catch (IOException ex) {

            System.out.println("error while processing file");
            System.out.println(ex);
        }

    }

    private String GetStringFromSet(Set<Integer> val) {
        var str="";
        Object[] array = val.toArray();
        Arrays.sort(array);
        for(var ele : array) {
            str =  str + (ele) + " ";
        }
//        Iterator value = array.iterator();
//        while (value.hasNext()) {
//          str =  str + (value.next()) + " ";
//        }
        return str;
    }

    @Override
    public void process(String line) {
        var resultarr = new long[bloomFilterColumns];
        var arr = line.split(" ");
        var currColumn = 0;

        for (int i= 0 ; i< bloomFilterColumns; i++ ) {
            if(i<arr.length) {
                var val = arr[i].replaceAll("[^-0-9]", "");

                if (val.length() > 0 && !val.isEmpty()) {
                    resultarr[currColumn++] = Long.parseLong(val);
                }
            }
        }
        bloomFilterArray.AddNewRow(resultarr);
    }

    private void CreateTermBitArray(List<String> queries, HashMap<String, Byte> termHashDic) {

        var hashFunctions =  InitializeHashFunctions((byte)maxNoHashFunctions);
        termArray = new BitArray2D(bloomFilterRows,queries.size());
        var queryIndex=0;
        for(String query: queries) {

            var hashVal = hashFunctions.get(termHashDic.get(query)).getHashValue(query);
            var termRow = Math.abs((int)hashVal % (bloomFilterRows));
            termArray.setValue(termRow, queryIndex++,true);
        }
    }

}
