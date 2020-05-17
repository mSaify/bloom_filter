

import java.io.File;
import java.io.IOException;
import java.lang.Math;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;

public class BloomFilter implements ReadFileOperation {

    private String termFilePath;
    private String freqPath;
    private String totalPath;
    private Byte[][] termMatrix;
    private double snr=0.0d;
    private double density=0.0d;
    private HashMap<String, Double> frequencyDict;
    private int totalDocuments = 0;
    FileOperations fileOperations;

    public  BloomFilter(String termDocumentMatrixFilePath, String termFreq, String totalFilesPath, double _snr, double _density)
    {

        termFilePath = termDocumentMatrixFilePath;
        freqPath=termFreq;
        totalPath=totalFilesPath;
        snr = _snr;
        density = _density;

    }

    public  BloomFilter(double _snr, double _density)
    {
        snr = _snr;
        density = _density;

    }

    public  BloomFilter(String[][] termDocumentMatrix,double _snr, double _density) {
        snr = _snr;
        density = _density;
    }

    private double NumnberOfHashFunctions(double freq) {
        var eq1 = freq/((1-freq)*snr);
        var eq2 = Math.log(eq1)/Math.log(density);
        return Math.min(3.0d, eq2);
    }

    private void GenerateFrequencyDic() {

        try {
            totalDocuments = Integer.parseInt(readFileAsString(totalPath));
            File file = new File(termFilePath);
            ReadFileOperation fileProcessing = this;
            fileOperations.readFileAndPerformOperation(file.getPath(), fileProcessing);

        }
        catch (IOException ex) {

            System.out.println("error while processing file");
            System.out.println(ex);
        }

    }

    public void create() {

        GenerateFrequencyDic();




    }

    public static String readFileAsString(String fileName)throws IOException
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }

    @Override
    public void process(String line) {
        var result = line.split(",");
        frequencyDict.put(result[0], Double.parseDouble(result[1])/totalDocuments  );
    }
}
