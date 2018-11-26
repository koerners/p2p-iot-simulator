package example.update.observation;

import example.update.NetworkAgent;
import example.update.constraints.NetworkRange;
import example.update.strategies.Storage;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;

public class SentDataObserver implements Control {

    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    /**
     * The protocol to look at.
     *
     * @config
     */
    private static final String PAR_SENTDATA_PROT = "protocol";

    /**
     * Output logfile name base.
     *
     * @config
     */
    private static final String PAR_FILENAME_BASE = "filename";

    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------

    /**
     * Energy protocol identifier. Obtained from config property
     * {@link #PAR_SENTDATA_PROT}.
     */
    private final int pid;

    /* writer handling file output. Name obtained from config
     * {@link #PAR_FILENAME_BASE}.
     */
    private final Writer writer;


    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------
    /**
     * Standard constructor that reads the configuration parameters. Invoked by
     * the simulation engine.
     *
     * @param prefix
     *            the configuration prefix for this class.
     */
    public SentDataObserver(String prefix) {

        pid = Configuration.getPid(prefix + "." + PAR_SENTDATA_PROT);
        writer = new Writer("raw_dat/"+Configuration.getString(prefix + "."
                + PAR_FILENAME_BASE, "sentdata_dump"));
    }

    // Control interface method. does the file handling
    public boolean execute() {
        StringBuilder log = new StringBuilder();
        log.append("Node;totalOut;totalIn;dataOut;dataIn;dataOut(Byte);dataIn(Byte);Request;Offer;Accept;Data;DataAck;Cancel;TellMe;ListResponds" + System.lineSeparator());

        for (int i = 0; i < Network.size(); i++) {

            NetworkAgent proto = (NetworkAgent) Network.get(i).getProtocol(pid);
            log.append(Network.get(i).getID() +  ";" + proto.counter.getOutgoingTotal() + ";" + proto.counter.getIngoingTotal() + ";" + proto.counter.getOutgoingData() + ";" + proto.counter.getIngoingData() + ";" + proto.counter.getDataOut() + ";" + proto.counter.getDataIn() + proto.counter.getTypeCount()).append(System.lineSeparator());
        }
        writer.write(log.toString());

        return false;
    }
}
