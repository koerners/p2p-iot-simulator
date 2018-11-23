package example.update;

import peersim.core.Node;

public class MessageCounter {

    private int outgoingTotal;
    private int ingoingTotal;
    private int outgoingData;
    private int ingoingData;

    private long dataIn;
    private long dataOut;


    public MessageCounter() {
        this.outgoingData = 0;
        this.ingoingData = 0;
        this.outgoingTotal = 0;
        this.ingoingTotal = 0;
        this.dataIn = 0;
        this.dataOut = 0;
    }

    public void outTotalIncrement() {
        outgoingTotal += 1;
    }

    public void inTotalIncrement() {
        ingoingTotal += 1;
    }

    public void outDataIncrement() {
        outgoingData += 1;
    }

    public void inDataIncrement() {
        ingoingData += 1;
    }

    public void addDataOut(long sizeInByte) {
        dataOut += sizeInByte;
    }

    public void addDataIn(long sizeInByte) {
        dataIn += sizeInByte;
    }

    public int getOutgoingTotal() {
        return outgoingTotal;
    }

    public int getIngoingTotal() {
        return ingoingTotal;
    }

    public int getOutgoingData() {
        return outgoingData;
    }

    public int getIngoingData() {
        return ingoingData;
    }

    public long getDataIn() {
        return dataIn;
    }

    public long getDataOut() {
        return dataOut;
    }
}
