package example.update.observation;

import example.update.NetworkAgent;

import example.update.Scheduler;
import org.nfunk.jep.function.Str;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class DisseminationObserver implements Control {

    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    /**
     * The protocol to look at.
     *
     * @config
     */
    private static final String PAR_NET = "transfer_protocol";
    private static final String PAR_SCHED = "sched_protocol";

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
     * {@link #}.
     */
    private final int netPid;
    private final int schedPid;

    /* logfile to print data. Name obtained from config
     * {@link #PAR_FILENAME_BASE}.
     */
    private final String filename;

    Writer progressOutput;

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
    public DisseminationObserver(String prefix) {

        netPid = Configuration.getPid(prefix + "." + PAR_NET);
        schedPid = Configuration.getPid(prefix + "." + PAR_SCHED);

        filename = "raw_dat/" +Configuration.getString(prefix + "."
                + PAR_FILENAME_BASE, "progress_dump");
        progressOutput = new Writer(filename);
    }

    // Control interface method. does the file handling
    public boolean execute() {

        Scheduler sched;
        HashSet<String> createdTasks = new HashSet<>();

        //get the list of jobs that exist in the network
        for (int i = 0; i < Network.size(); i++) {

            sched = ((Scheduler) Network.get(i).getProtocol(schedPid));
            sched.getJobList().forEach(item ->{
                    createdTasks.add(item);
            });
        }

        // we need sorting consistency
        List<String> sortedList = new ArrayList(createdTasks);
        Collections.sort(sortedList);

        //create the header of the file
        StringBuilder out = new StringBuilder();
        out.append("id;");
        sortedList.forEach(item -> {
            out.append(item).append(";");
        });
        out.append(System.lineSeparator());

        // create an output log file with progress for each jobID.
        for (int i = 0; i < Network.size(); i++) {

            out.append(i+";");
            Node current = Network.get(i);

            sortedList.forEach(item -> {
                String progress = ((NetworkAgent) current.getProtocol(netPid)).jobProgress(item);

                if (! progress.isEmpty() ) {
                    out.append(progress).append(";");
                }
                else {
                    out.append("0;");
                }
            });
            out.append(System.lineSeparator());
        }
        progressOutput.write(out.toString());
    return false;
    }
}