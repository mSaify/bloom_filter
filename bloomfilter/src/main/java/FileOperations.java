

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public  class FileOperations {

    String InputPath = "/Users/msaify/repos/IITHAssignments/SEMESTER-2/Scalable Algorithms/assignment1/20_newsgroups";
    String OutputPath = "/Users/msaify/repos/IITHAssignments/SEMESTER-2/Scalable Algorithms/assignment1/20_newsgroups/allFiles";

    File outputDirectory = null;

    public void copyFileToSingleFolder(String inputPath, String outputPath) throws IOException {
        InputPath=inputPath;
        OutputPath=outputPath;

        outputDirectory = new File(OutputPath);
       File [] files = new File(InputPath).listFiles();
       if (files != null) {


           for (File file : files) {
               if (file.isDirectory()) {
                   System.out.println("Directory: " + file.getName());
                   copyFilesFromFolder(file.listFiles()); // Calls same method again.
               } else {
                   System.out.println("File: " + file.getName());
               }
           }
       }


    }

    private void copyFilesFromFolder(File[] files) throws IOException {

        for(File file : files) {

            if(file.isDirectory()) {
                System.out.println("Copying files from this directory: " + file.getName());
                copyFilesFromFolder(file.listFiles());
            }
            else {
                FileUtils.copyFileToDirectory(file.getAbsoluteFile(), outputDirectory);
            }
        }

    }

    public void readFileAndPerformOperation(String filepath, ReadFileOperation processFileOperation) throws IOException {

        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filepath);
            sc = new Scanner(inputStream, "UTF-8");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                processFileOperation.process(line);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

    }


    public static void writeByte(byte[] bytes,String file)
    {
        try {

            // Initialize a pointer
            // in file using OutputStream
            OutputStream
                    os
                    = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(bytes);
            // Close the file
            os.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
