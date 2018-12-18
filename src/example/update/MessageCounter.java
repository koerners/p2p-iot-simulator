package example.update;

import java.util.Map;

import static example.update.DataMessage.*;

public class MessageCounter {

    private long outgoingTotal;
    private long ingoingTotal;
    private long outgoingData;
    private long ingoingData;

    private long dataIn;
    private long dataOut;


    private long request_In;
    private long offer_In;
    private long accept_In;
    private long data_In;
    private long dataack_In;
    private long cancel_In;
    private long tellme_In;
    private long listresponse_In;

    private long request_Out;
    private long offer_Out;
    private long accept_Out;
    private long data_Out;
    private long dataack_Out;
    private long cancel_Out;
    private long tellme_Out;
    private long listresponse_Out;


    public MessageCounter() {
        this.outgoingData = 0;
        this.ingoingData = 0;
        this.outgoingTotal = 0;
        this.ingoingTotal = 0;
        this.dataIn = 0;
        this.dataOut = 0;

        this.request_In = 0;
        this.offer_In = 0;
        this.accept_In = 0;
        this.data_In = 0;
        this.dataack_In = 0;
        this.cancel_In = 0;
        this.tellme_In = 0;
        this.listresponse_In = 0;

        this.request_Out = 0;
        this.offer_Out = 0;
        this.accept_Out = 0;
        this.data_Out = 0;
        this.dataack_Out = 0;
        this.cancel_Out = 0;
        this.tellme_Out = 0;
        this.listresponse_Out = 0;

    }

    public long getOutgoingTotal() {
        return outgoingTotal;
    }

    public long getIngoingTotal() {
        return ingoingTotal;
    }

    public long getOutgoingData() {
        return outgoingData;
    }

    public long getIngoingData() {
        return ingoingData;
    }

    public long getDataIn() {
        return dataIn;
    }

    public long getDataOut() {
        return dataOut;
    }

    public String getTypeCount() {
        return ";" + request_In + "/" + request_Out + ";" + offer_In + "/" + offer_Out + ";" + accept_In + "/" + accept_Out + ";" + data_In + "/" + data_Out + ";" + dataack_In + "/" + dataack_Out + ";" + cancel_In + "/" + cancel_Out + ";" + tellme_In + "/" + tellme_Out + ";" + listresponse_In + "/" + listresponse_Out;
    }

    void overallCounter(boolean in, DataMessage msg, int pieceSize) {

        long size = 0;


        if (in) {
            ingoingTotal++;

            switch (msg.type) {
                case REQUEST:
                    request_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case OFFER:
                    offer_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case ACCEPT:
                    accept_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case DATA:
                    data_In++;
                    ingoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber + pieceSize;
                    break;

                case DATAACK:
                    dataack_In++;
                    ingoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case CANCEL:
                    cancel_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case TELLME:
                    tellme_In++;
                    size = 1;
                    break;

                case LISTRESPONSE:
                    listresponse_In++;
                    ingoingData++;

                    size += msg.hash.length() * 3;

                    for (Map.Entry<String, boolean[]> of : msg.offers) {
                        size += of.getKey().length() * 3;
                        size += of.getValue().length * 4;
                    }

                    break;

            }
            dataIn+= size;
        } else {
            outgoingTotal++;

            switch (msg.type) {
                case REQUEST:
                    request_Out++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    break;

                case OFFER:
                    offer_Out++;
                    outgoingData++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;


                    break;

                case ACCEPT:
                    accept_Out++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;


                    break;

                case DATA:
                    data_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber + pieceSize;


                    break;

                case DATAACK:
                    dataack_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;

                    break;

                case CANCEL:
                    cancel_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;

                    break;

                case TELLME:
                    tellme_Out++;
                    size = 1;

                    break;

                case LISTRESPONSE:
                    listresponse_Out++;
                    outgoingData++;

                    size += msg.hash.length() * 3;
                    for (Map.Entry<String, boolean[]> of : msg.offers) {
                        size += of.getKey().length() * 3;
                        size += of.getValue().length * 4;
                    }
                    break;

            }
            dataOut += size;
        }


    }
}




