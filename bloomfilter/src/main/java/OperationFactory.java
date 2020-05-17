

public class OperationFactory {

    public  Operation getOperation(ArgParser args) {

        OperationEnum operation = args.executeOperation;
        switch (operation) {
            case Index: { return new Indexer(args.density, args.snr, args.srcFolderPath); }
            case Search: { return new Search(args.queryFile);  }
            default: return null;
        }

    }
}
