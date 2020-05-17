

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.*;

public class ReportGeneration implements Operation {

    HashMap<String, Set<Integer>> result;

    String expectedFilePath = "expectedResult.txt";

    Integer totalHits =0;
    Integer expectedHits =0;

    ReportGeneration(HashMap<String, Set<Integer>> _result ) {

        this.result = _result;
    }

    public static void main(String args[]){
        Top10000QueryGenerator();
    }

    @Override
    public void execute() {
        var falsePositive = ReadExpected();
        System.out.println(String.format("The false positive rate for the  %s terms is  %s" , 10000, falsePositive));


    }

    private float ReadExpected() {
        var termHashDict = new HashMap<String, HashSet<Integer>>();

        totalHits = 0;
        expectedHits = 0;

        try {
            File file = new File(expectedFilePath);
            FileReader fileReader = new FileReader(file, Charset.defaultCharset());
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var arr =line.split(" ");
                var term = arr[0];

                if(term!= "" && !term.contains(" ") && !termHashDict.containsKey(term)) {

                    var actualVal= result.get(term);
                    if(actualVal!=null && actualVal.size()>0) {
                        for (int i = 1; i < arr.length; i++) {
                            var res = arr[i].replaceAll("\"[^0-9]\"", "");
                            if (!res.isEmpty()) {
                                totalHits = totalHits + 1;
                                try {
                                    var expV = Integer.parseInt(arr[i]);
                                    if (actualVal.contains(expV)) {
                                        expectedHits++;
                                    }
                                } catch (Exception ex) {

                                }

                            }
                        }
                    }
                }
            }

        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return totalHits>0 ? 1- ((expectedHits*1.0f)/(totalHits*1.0f)) : 0;
    }


    public static void Top10000QueryGenerator() {


        try {

            File file = new File("expectedResult.txt");
            FileReader fileReader = new FileReader(file, Charset.defaultCharset());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> list = new ArrayList<String>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                var arr =line.split(" ");
                var term = arr[0];
                if(!term.isEmpty())
                    list.add(term);

            }

            Random rand = new Random();

            FileWriter f2 = new FileWriter("queryFile10000");

            var size = list.size()-1;
            for(int i=0;i<10000;i++) {
                f2.write(list.get(rand.nextInt(size)));
                f2.write("\n");
            }
            f2.close();


        }
        catch (Exception ex) {
            System.out.println(ex);
        }


    }

}
