

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TermHashFunctionsGenerator implements ReadFileOperation, Operation {

    Double SNR;
    Double Density;
    String folderPath;
    HashMap<String, HashSet<Integer>> termDocumentMatrix;
    public HashMap<String, Byte> termHashFunctionDict;
    FileOperations fileOperations;
    HashMap<Integer,String> docInfo;
    public Integer currDocCount;


    public Integer getNumberOfDocuments() {
        return currDocCount;
    }


    public  TermHashFunctionsGenerator(Double density, Double snr, String srcfolderPath) {

        SNR = snr;
        Density = density;
        fileOperations = new FileOperations();
        currDocCount=0;
        folderPath=srcfolderPath;
        termDocumentMatrix= new HashMap<String, HashSet<Integer>>();
        termHashFunctionDict = new HashMap<String, Byte>();
        docInfo = new HashMap<Integer, String>();

    }

    @Override
    public void execute() {
        File [] files = new File(folderPath).listFiles();
        processFiles(files);
    }

    private void processFiles(File[] files) {
        Arrays.sort(files);
        for(File file:files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                processFiles(file.listFiles());
            } else {
                processSingleFile(file);
            }
        }

        try {
            StoreDocumentsLookUp();
            CreateAndStoreTermMatrix("expectedResult.txt", "term_hash_lookup.txt", "totaldocuments.txt");

        }
        catch (Exception ex ) {
            System.out.println("failed while storing term freq and matrix");
            System.out.println(ex);
        }

    }

    private void StoreDocumentsLookUp() {

        try {
            System.out.println("Storing the docid and document_name lookup --- document_lookup.txt");

            FileWriter f4 = new FileWriter("document_lookup.txt");
            for(Map.Entry<Integer, String> entry : docInfo.entrySet()) {
                f4.write(String.format("%s|%s \n", entry.getKey(), entry.getValue()));
            }
            f4.close();
        }
        catch (Exception ex) {
                System.out.println("Unable to store the document lookup" + ex.toString() );
        }
    }

    private void CreateAndStoreTermMatrix(String matrixFile, String termFreq, String total) throws IOException {

        FileWriter f2 = new FileWriter(total);
        f2.write(currDocCount.toString());
        f2.close();

        PrintWriter f0 = new PrintWriter(matrixFile);
        BufferedWriter f1 = new BufferedWriter(new FileWriter(termFreq));

        System.out.println("Storing the term hash Function file  --- term_hash_lookup.txt ");

        for (Map.Entry<String, HashSet<Integer>> item : termDocumentMatrix.entrySet()) {
            f0.print(String.format("%s ", (StemmerWrapper.getStemmedWords(item.getKey()))));
            for(var val:item.getValue()) {
                f0.print(String.format("%s ", (val)));
            }
            f0.println();
            var term = item.getKey();
            var hashFunctions = NumberOfHashFunctions(item.getValue().size());
            termHashFunctionDict.put(term, hashFunctions);


            //System.out.println(String.format("%s,%s,%s \n", item.getKey(), item.getValue().size(), hashFunctions ));
            //f1.write(String.format("%s,%s,%s \n", item.getKey(), item.getValue().size(), hashFunctions ));
            f1.write(String.format("%s|%s \n", item.getKey(), hashFunctions ));

        }

        f1.close();
        f0.close();

    }

    private byte NumberOfHashFunctions(Integer totalDoc) {

        Double freq = totalDoc*1.0d/currDocCount;
        //System.out.println(freq);
        var eq1 = freq/((1-freq)*SNR);
        var eq2 = (Math.log(eq1))/(Math.log(Density));
        if(eq2<=0.0) {
            return 3;
        }
        else {
            //System.out.println(String.format("%s,%s ", SNR, Density));
            //System.out.println(Math.round(Math.max(3, eq2)));
            return (byte) Math.max(3, eq2);
        }

    }

    private void processSingleFile(File file) {
        ReadFileOperation fileProcessing = this;
        //System.out.println(this);
        try {
            fileOperations.readFileAndPerformOperation(file.getPath(), fileProcessing);
            UpdateDocumentInformation(file);
        }
        catch (IOException ex) {

            System.out.println("error while processing file");
            System.out.println(ex);
        }

    }

    private void UpdateDocumentInformation(File file) {
        //System.out.println(file.getName());
        if (!docInfo.containsKey(file.getName())) {
            docInfo.put(currDocCount++, file.getName());
        }
    }

    @Override
    public void process(String line) {
        String [] words = StemmerWrapper.getStemmedWords(line.toLowerCase()).split(" ");
        this.addToTermDictionary(words);
    }

    private void addToTermDictionary(String[] words) {
        for (String word : words) {
            var newWord = word.replaceAll("[^a-zA-Z0-9]", "");
            if (!newWord.isBlank()) {
                if (termDocumentMatrix.containsKey(word)) {
                    var curr = termDocumentMatrix.get(word);
                    curr.add(currDocCount);
                    termDocumentMatrix.put(word, curr);
                } else {
                    HashSet<Integer> newSet = new HashSet();
                    newSet.add(currDocCount);
                    termDocumentMatrix.put(word, newSet);
                }
            }
        }
    }
}
