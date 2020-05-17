
import java.io.*;
import java.util.*;

public class Indexer implements Operation {

    Double SNR;
    Double Density;
    String folderPath;
    HashMap<String, HashSet<Integer>> frequencyDict;
    FileOperations fileOperations;
    HashMap<Integer,String> docInfo;
    Integer currDocCount;
    int[] randomSeeds;

    public  Indexer(Double density, Double snr, String srcfolderPath) {

        SNR = snr;
        Density = density;
        fileOperations = new FileOperations();
        currDocCount=0;
        folderPath=srcfolderPath;
        HashMap<String,Byte> termHashFunctionDict = new HashMap<String,Byte>();
        randomSeeds = new int[] { 1234, 4321, 6789, 9876, 3456, 6543 };

    }

    @Override
    public void execute() {

        System.out.println("doc index creation and storage of relevant files started at ");

        long start = System.currentTimeMillis();

      var termHashFunctionsGenerator  =  new TermHashFunctionsGenerator(Density, SNR, folderPath);
      termHashFunctionsGenerator.execute();

      var termHashDic = termHashFunctionsGenerator.termHashFunctionDict;
      var termDocumentMatrix = termHashFunctionsGenerator.termDocumentMatrix;

      var maxHashFunctions = Collections.max(termHashDic.values());
      var noOfDocuments = termHashFunctionsGenerator.getNumberOfDocuments();

      termHashFunctionsGenerator=null;
      var noOfTerms = termDocumentMatrix.size();
      var hashFunctions =  InitializeHashFunctions(maxHashFunctions);

      var arr = Get2DArray(termHashDic.size(),maxHashFunctions, noOfDocuments);
      var arrSizeOfTermsBit = (int)arr.getSize()[0];

      System.out.println(String.format("number of terms %s and docs %s", noOfTerms, noOfDocuments ));
      //System.out.println(String.format("2d array size %s %s", arr.getSize()[0], arr.getSize()[1]));

        for (Map.Entry<String, HashSet<Integer>> item : termDocumentMatrix.entrySet()) {

            var term =  item.getKey();
            byte noOfHashFunctions = termHashDic.get(item.getKey());

            for (var document : item.getValue()) {

                for(int i= 0;i<noOfHashFunctions;i++){

                    var hashVal = hashFunctions.get(i).getHashValue(term);
                    var termRow = Math.abs((int)hashVal % (arrSizeOfTermsBit));
                    //System.out.println(String.format("term %s hashFunctions %s rowSet %s for document %d",term, hashVal, termRow, document));
                    arr.setValue(termRow, document,true);
                }
            }
        }
        var arrSize = arr.getSize();
        var totalSize = (arrSize[0] * arrSize[1] * 8) / (1024*1024);
        System.out.println("total array size without bit slicing  --- "  + totalSize + " in MB");

        arrSize = arr.getActualSize();
        totalSize = (arrSize[0] * arrSize[1] * 8) / (1024*1024);
        System.out.println("array size with bit slicing  --- "  + totalSize + " in MB");

        arr.save2DArrayToFile("bloomFilter");

        long end = System.currentTimeMillis();



        System.out.println("doc_index took  " + (end - start) / 1000f + " seconds");
    }

    private BitArray2D Get2DArray(long termHashDictSize, byte maxHashFunctions, int noOfDocuments) {

        var termArraySize = GetTermArraySize(termHashDictSize,maxHashFunctions);

        return new BitArray2D(termArraySize,noOfDocuments);


    }

    private List<HashWrapper> InitializeHashFunctions(byte maxHashFunctions) {

        List<HashWrapper> hashFunctions = new ArrayList<>();

        for (int i=0; i<maxHashFunctions; i++) {
            hashFunctions.add(new HashWrapper(randomSeeds[i]));
        }

        return hashFunctions;
    }


    private int GetTermArraySize(long totalTerms,byte maxHashFunctions) {
        return (int) Math.round((totalTerms * maxHashFunctions)/Math.log(2));

    }
}



