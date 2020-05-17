import java.io.*;


public class Main {



    public  static  void main(String args[]) {

        ArgParser arguments = new ArgParser(args);
        Operation op = new OperationFactory().getOperation(arguments);
        op.execute();

        // new ReportGeneration(null).Top10000QueryGenerator();
        //  FileOperations(args); //all copying operations or changing directory locations performed here


    }

    public static void FileOperations(String args[]) {

        String InputPath = args[1];
        String OutputPath = "./testFiles";

        try {

            FileOperations fileOp = new   FileOperations();
            fileOp.copyFileToSingleFolder(InputPath,OutputPath);
        }
        catch (IOException ex) {
            System.out.println("Unable to preform copy operation");

        }
    }
}
