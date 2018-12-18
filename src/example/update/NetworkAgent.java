package example.update;

import example.update.constraints.Bandwidth;
import example.update.strategies.Energy;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;


public class NetworkAgent implements EDProtocol, CDProtocol {

    // ------------------------------------------------------------------------
    // Parameters
    // ------------------------------------------------------------------------

    // Protocol to interact with : softwareDB, neighborhood maintainer and Supervisor
    private static final String NEIGH_PROT = "neighborhood_protocol";
    private static final String ENERGY_PROT = "energy_protocol";
    private static final String BANDW = "bandwidth_protocol";
    private static final String PIECE_SIZE = "piece_size";


    // ------------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------------
    private final int neighborhoodPID;
    private final int powerSourcePID;
    private final int pieceSize;
    public long countMessages = 0;
    public long sentData = 0;
    public long dataDownloaded = 0;
    public long overhead = 0;
    // my local list <SoftwarePackage.ID, array>
    List<Map.Entry<String, boolean[]>> localData;
    List<Map.Entry<Node, Map.Entry<String, boolean[]>>> otherNodesData;
    boolean tellMeRequestSent = false;
    //List<Map.Entry<String, int[]>> other;
    // constructor
    String prefix;
    private int bandwidthPid;
    private boolean downloading = false;


    public NetworkAgent(String prefix) {
        this.prefix = prefix;

        // get PIDs of SoftwareDB and Downloader
        neighborhoodPID = Configuration.getPid(prefix + "." + NEIGH_PROT);
        powerSourcePID = Configuration.getPid(prefix + "." + ENERGY_PROT);
        bandwidthPid = Configuration.getPid(prefix + "." + BANDW);
        pieceSize = Configuration.getInt(prefix + "." + PIECE_SIZE);

        localData = new ArrayList<>();
        otherNodesData = new ArrayList<>();
        counter = new MessageCounter();

    }

    public MessageCounter counter;


    public NetworkAgent clone() {
        return new NetworkAgent(prefix);
    }


    // method trigerred by scheduler -> gives an ordered list of softwares to DL.
    //TODO : do something cleaner in a softwareDB class

    public void update(List<SoftwareJob> packages) {

        if (localData.isEmpty()) {
            localData.add(new SimpleEntry(packages.get(0).getId(),
                    new boolean[(int) Math.ceil((double) packages.get(0).size / (double) pieceSize)]));
            Arrays.fill(localData.get(0).getValue(), Boolean.FALSE);
        } else {
            // update the local list.
            for (int i = 0; i < packages.size(); i++) {
                for (int j = 0; j < localData.size(); j++) {
                    if (packages.get(i).getId().equals(localData.get(j).getKey())) {
                        localData.add(i, localData.remove(j));
                    } else if (!localDataContains(packages.get(i).getId())) {
                        localData.add(i, new SimpleEntry(packages.get(i).getId(),
                                new boolean[(int) Math.ceil((double) packages.get(i).size / (double) pieceSize)]));
                        Arrays.fill(localData.get(i).getValue(), Boolean.FALSE);
                    }
                }
            }
        }
    }


    public void clearOther() {
        if (otherNodesData != null) {
            otherNodesData.clear();
        }
    }


    private Map.Entry<String, Integer> getDownload(Node localNode, int pid) {

        //TODO something cleaner in a separate function.
//        boolean nothingToDo = true;
//        for (Map.Entry<String, boolean[]> localDl : localData) {
//            nothingToDo = isCompleted(localDl.getValue());
//        }
//        if (nothingToDo){
//            //System.out.println("doing ntg");
//            return null;
//        }


        if (!tellMeRequestSent) {
            DataMessage askForData = new DataMessage(DataMessage.TELLME, "null", 0, localNode, this.localData);
            requestNeighbors(localNode, askForData, pid);
            counter.overallCounter(false, askForData, pieceSize);
            //commonstate.gettime
            tellMeRequestSent = true;
            return null;
        }

        if (otherNodesData.isEmpty()) {
            clearOther();
            tellMeRequestSent = false;
        } else {

            if (!otherNodesData.isEmpty()) {

                List<Map.Entry<Node, Map.Entry<String, boolean[]>>> otherNodesDataCopy = new LinkedList<>();
                otherNodesDataCopy.addAll(otherNodesData);


                switch (methodForChoosingDownloadPiece()) {
                    case 1:
                        //get what's available
                        for (Map.Entry<Node, Map.Entry<String, boolean[]>> availableDl : otherNodesDataCopy) {

                            for (Map.Entry<String, boolean[]> localDl : localData) {
                                // first we search a matching download.
                                if (localDl.getKey().equals(availableDl.getValue().getKey())) {
                                    //then select a piece to download among the offered ones.

                                    for (int i = 0; i < availableDl.getValue().getValue().length; i++) {
//                                        System.out.println("remote (" + availableDl.getKey().getID() + ")=" + availableDl.getValue().getValue()[i] + ";"
//                                                + " local(" + localNode.getID() + ")=" + localDl.getValue()[i]);

                                        if (availableDl.getValue().getValue()[i] == true && localDl.getValue()[i] == false) {
                                            System.out.println("Get available in node " + localNode.getID() + " picked piece number: " + i + " from node " + availableDl.getKey().getID());
                                            //clearOther();
                                            //otherNodesDataCopy.clear();
                                            return new SimpleEntry<>(localDl.getKey(), i);
                                        }
                                    }
                                    // No useful piece in that answer
                                    otherNodesData.remove(availableDl);
                                }
                            }
                        }
                        break;

                    case 2:
                        //Rarest first
                        List<Map.Entry<String, int[]>> counter = new ArrayList<>();
                        String minJob = null;
                        int minIndex = 0;
                        int min = Integer.MAX_VALUE;

                        for (Map.Entry<String, boolean[]> einsJob : localData) {
                            String job = einsJob.getKey();
                            int[] arr;
                            arr = new int[einsJob.getValue().length];
                            counter.add(new SimpleEntry<>(job, arr));
                        }

                        for (Map.Entry<Node, Map.Entry<String, boolean[]>> availableDl : otherNodesDataCopy) {

                            for (Map.Entry<String, int[]> cow : counter) {
                                if (cow.getKey().equals(availableDl.getValue().getKey())) {
                                    for (int i = 0; i < availableDl.getValue().getValue().length; i++) {

                                        if (availableDl.getValue().getValue()[i] == true) {
                                            cow.getValue()[i]++;

                                        }
                                    }
                                }
                            }
                        }
                        for (int i = 0; i < counter.size(); i++) {
                            for (int j = 0; j < counter.get(i).getValue().length; j++) {
                                if ((min > counter.get(i).getValue()[j]) && (counter.get(i).getValue()[j] > 0)) {
                                    if (localData.get(i).getValue()[j] == false) {
                                        min = counter.get(i).getValue()[j];
                                        minIndex = j;
                                        minJob = counter.get(i).getKey();
                                    }
                                }
                            }

                        }
                        if (minJob != null) {
                            System.out.println("Rarest first: JOB: " + minJob + " INDEX:  " + minIndex + " VALUE:  " + min);
                            return new SimpleEntry<>(minJob, minIndex);
                        }

                        break;


                    case 3:
                        //request missing pieces

                        for (int i = 0; i < this.localData.size(); i++) {
                            for (int j = 0; j < this.localData.get(i).getValue().length; j++) {
                                if (this.localData.get(i).getValue()[j] == false) {
                                    System.out.println("Endgame " + localData.get(i).getKey() + "  " + j);
                                    return new SimpleEntry<>(this.localData.get(i).getKey(), j);
                                }
                            }
                        }
                        break;
                }


                // received responses don't match anything locally or have nothing of interest. Reset everything.
                clearOther();
                otherNodesDataCopy.clear(); //Necessary?

                tellMeRequestSent = false;
            }
        }
        return null;
    }

    private int methodForChoosingDownloadPiece() {
        //TODO: Something more sophisticated
        //System.out.println("JOB PROGRESS: "+jobProgress());
        int allProgress = 0;

        for (int i = 0; i < jobProgress().size(); i++) {
            allProgress += (int) jobProgress().get(i);
        }
        allProgress = allProgress / jobProgress().size();
        //System.out.println("PROGRESS: " +allProgress);

        if (allProgress < 10) return 1;
        if (allProgress < 90) return 2;
        if (allProgress <= 100) return 3;

        else return 0;
    }


    private void usePower(int multiplier, Node node) {
        ((Energy) node.getProtocol(powerSourcePID)).consume(multiplier);
        overhead += multiplier;
    }

    private boolean isStillAround(Node neighbor, Node local) {
        return ((NeighborhoodMaintainer) local.getProtocol(neighborhoodPID)).getNeighbors().contains(neighbor);
    }

    private boolean isCompleted(boolean[] array) {
        for (boolean value : array) {
            if (value == false)
                return false;
        }
        return true;
    }


    public void nextCycle(Node localNode, int pid) {
        if (!downloading && !localData.isEmpty()) {

            //clearOtherNodes();


            Map.Entry<String, Integer> toDownload = getDownload(localNode, pid);

//            System.out.println("OTHER NODES: "+otherNodes);

            if (toDownload != null) {
                //craft a new message
//                System.out.println("KEY: " +toDownload.getKey() +" VALUE: " +toDownload.getValue());
                DataMessage msg = new DataMessage(DataMessage.REQUEST, toDownload.getKey(), toDownload.getValue(), localNode, localData);
                counter.overallCounter(false, msg, pieceSize);
                requestNeighbors(localNode, msg, pid);

            }
        }
    }

    //receiving data from other peers
    public void processEvent(Node localNode, int pid, Object data) {

        DataMessage event = (DataMessage) (data);
        if (event.sender == localNode) {
            return;
        }

        counter.overallCounter(true, event, pieceSize);


        switch (event.type) {

            case DataMessage.REQUEST:

                if (!downloading && localDataContains(event.hash)) {

                    // is this piece complete
                    if (localData.get(getLocalIndex(event.hash)).getValue()[event.pieceNumber]) {
                        DataMessage reply = new DataMessage(DataMessage.OFFER, event.hash, event.pieceNumber, localNode, null);
                        EDSimulator.add(1, reply, event.sender, pid);
                        counter.overallCounter(false, reply, pieceSize);
                        usePower(1, localNode);
                    }
                }
                break;

            case DataMessage.TELLME:


                DataMessage sendInfo = new DataMessage(DataMessage.LISTRESPONSE, event.hash, event.pieceNumber, localNode, localData);
                EDSimulator.add(1, sendInfo, event.sender, pid);
                counter.overallCounter(false, sendInfo, pieceSize);

                break;


            case DataMessage.LISTRESPONSE:


                for (Map.Entry<String, boolean[]> senderDataEntry : event.offers) {
                    otherNodesData.add(new SimpleEntry<>(event.sender, senderDataEntry));
                }
                break;

            case DataMessage.OFFER:


                //downloading data
                if (!downloading) {
                    this.downloading = true;
                    //send ACCEPT
                    dataDownloaded += pieceSize;
                    DataMessage accept = new DataMessage(DataMessage.ACCEPT, event.hash, event.pieceNumber, localNode, localData);
                    EDSimulator.add(1, accept, event.sender, pid);
                    counter.overallCounter(false, accept, pieceSize);


                    usePower(1, localNode);
                }
                break;

            case DataMessage.ACCEPT:

                if (!downloading) {
                    //System.out.println("Node "+ localNode.getID() +" piece "+event.pieceNumber+ "receivied ACCEPT from node "+event.sender.getID());
                    //upload the data
                    this.downloading = true;
                    DataMessage dataMsg;

                    // use the lowest bandwith of the 2 nodes
                    long localUplink = ((Bandwidth) localNode.getProtocol(bandwidthPid)).getUplinkCapacity();
                    long remoteDownlink = ((Bandwidth) event.sender.getProtocol(bandwidthPid)).getDownlinkCapacity();

                    long bdw = localUplink <= remoteDownlink ? localUplink : remoteDownlink;

                    sentData += pieceSize;

                    dataMsg = new DataMessage(DataMessage.DATA, event.hash, event.pieceNumber, localNode, null);
                    EDSimulator.add(pieceSize / bdw, dataMsg, event.sender, pid);
                    counter.overallCounter(false, dataMsg, pieceSize);

                    //consume energy
                    usePower((int) (pieceSize / bdw), localNode);

                } else { // send a cancel message
                    DataMessage dataMsg;

                    dataMsg = new DataMessage(DataMessage.CANCEL, event.hash, event.pieceNumber, localNode, null);
                    EDSimulator.add(1, dataMsg, event.sender, pid);
                    counter.overallCounter(false, dataMsg, pieceSize);

                    usePower(1, localNode);
                }
                break;

            case DataMessage.DATA:

                //mark piece as completed into DB
                if (isStillAround(event.sender, localNode)) {
                    localData.get(getLocalIndex(event.hash)).getValue()[event.pieceNumber] = true;
                }
                this.downloading = false;
                //send ack
                DataMessage msg = new DataMessage(DataMessage.DATAACK, event.hash, event.pieceNumber, localNode, null);
                EDSimulator.add(1, msg, event.sender, pid);
                counter.overallCounter(false, msg, pieceSize);


//
                usePower(1, localNode);
                break;

            //TODO : what if energy cuts during a transfer? seeder is locked.
            case DataMessage.DATAACK:
                //System.out.println("node "+ localNode.getID() +" piece "+event.pieceNumber +" DATAACK message from node" + event.sender.getID());
                //we can stop uploading


                this.downloading = false;
                break;

            case DataMessage.CANCEL:

                this.downloading = false;
                // try again

                this.nextCycle(localNode, pid);
                break;


        }
    }

    private boolean localDataContains(String hash) {
        for (Map.Entry<String, boolean[]> tuple : localData) {
            if (hash.equals(tuple.getKey())) {
                return true;
            }
        }
        return false;
    }

    private int getLocalIndex(String hash) {
        for (int i = 0; i < localData.size(); i++) {
            if (hash.equals(localData.get(i).getKey())) {
                return i;
            }
        }
        return -1;
    }

    // send REQUEST messsages
    private void requestNeighbors(Node localNode, DataMessage msg, int pid) {

        //get neighbors list
        ((NeighborhoodMaintainer) localNode.getProtocol(neighborhoodPID)).getNeighbors()
                .forEach(neighbor -> {
                    // send request message to neighbor with no latency
                    EDSimulator.add(0, msg, neighbor, pid);
                    usePower(1, localNode);
                });
    }


    public List<Map.Entry<String, boolean[]>> getLocalData() {
        return localData;
    }

    public String jobProgress(String hash) {
        if (localDataContains(hash)) {
            boolean[] tab = localData.get(getLocalIndex(hash)).getValue();
            int perc = 0;
            for (boolean b : tab) {
                if (b) perc++;
            }
            return String.valueOf((int) (((double) perc / (double) tab.length) * 100));
        } else return "";
    }

    //TODO : better looking hashes
    public ArrayList jobProgress() {
        ArrayList<Integer> progress = new ArrayList<>();
        for (Map.Entry<String, boolean[]> entry : localData) {

            boolean[] tab = entry.getValue();
            int perc = 0;
            for (boolean b : tab) {
                if (b) perc++;
            }
            progress.add((int) (((double) perc / (double) tab.length) * 100));
        }
        return progress;
    }

    //complete a job : hook for the initializer
    public void completeJob(SoftwareJob job) {
        if (!localDataContains(job.getId())) {

            boolean[] data = new boolean[(int) Math.ceil((double) job.size / (double) pieceSize)];
            Arrays.fill(data, Boolean.TRUE);

            localData.add(new SimpleEntry(job.getId(), data));
        } else {
            Arrays.fill(localData.get(getLocalIndex(job.getId())).getValue(), Boolean.TRUE);
        }
    }
}

class DataMessage {

    final static int REQUEST = 0;
    final static int OFFER = 1;
    final static int ACCEPT = 2;
    final static int DATA = 3;
    final static int DATAACK = 4;
    final static int CANCEL = 5;
    final static int TELLME = 6;
    final static int LISTRESPONSE = 7;


//    enum type {REQUEST, OFFER, ACCEPT, DATA, DATAACK, CANCEL, TELLME, LISTRESPONSE}

    int type;
    String hash;
    int pieceNumber;
    List<Map.Entry<String, boolean[]>> offers;
    Node sender;


    public DataMessage(int type, String hash, int pieceNumber, Node sender, List<Map.Entry<String, boolean[]>> offers) {
        this.sender = sender;
        this.type = type;
        this.hash = hash;
        this.pieceNumber = pieceNumber;
        this.offers = offers;
    }
}
