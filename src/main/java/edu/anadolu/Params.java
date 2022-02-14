package edu.anadolu;

import com.lexicalscope.jewel.cli.Option;

public interface Params {

    @Option(description = "number of depots", shortName = "d", longName = "depots", defaultValue = "5")
    int getNumDepots();

    @Option(description = "number of salesmen per depot", shortName = {"s"}, longName = {"salesmen", "vehicles"}, defaultValue = "2")
    int getNumSalesmen();

    @Option(description = "use city names when displaying/printing", shortName = "v", longName = "verbose")
    boolean getVerbose();

    @Option(description = "initial city for nearest neighbour", shortName = "i", longName = {"initial", "start"}, defaultValue = "38")
    int getInitial();

    @Option(description = "choice for random or nearest neighbour solution", shortName = "c", longName = "choice")
    String choose();

    @Option(helpRequest = true, description = "display help", shortName = "h")
    boolean getHelp();

}
