


public class ArgParser {



    String srcFolderPath = "";

    Double density = null;

    Double snr = null;

    String queryFile = "";

    OperationEnum executeOperation;

    public ArgParser(String[] args) {

        executeOperation = OperationEnum.Index;

        try {
            if (args.length>0 && args[0].contains("search")) {
                executeOperation = OperationEnum.Search;
                queryFile=args[1];

            } else if (args.length>0) {
                executeOperation = OperationEnum.Index;
                if (args.length>1) {
                    srcFolderPath = args[1];
                    density =  Double.parseDouble(args[2]);
                    snr = Double.parseDouble(args[3]);
                }
            }

            System.out.println("operation to be executed " + this.executeOperation);
            if(this.srcFolderPath!="")
            System.out.println("folder path for indexing if passed " + this.srcFolderPath);

            if(this.queryFile!="")
            System.out.println("query file if passed " + this.queryFile);
            if(this.density!=null)
            System.out.println("snr if passed " + this.snr);
            if(this.snr!=null)
            System.out.println("d (density) if passed " + this.density);
        }
        catch (Exception ex ) {
            System.out.println("Some arugments are invalid. Please Check");
            System.out.println(ex);
        }
    }
}
