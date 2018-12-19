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

    private long request_dataIn;
    private long offer_dataIn;
    private long accept_dataIn;
    private long data_dataIn;
    private long dataack_dataIn;
    private long cancel_dataIn;
    private long tellme_dataIn;
    private long listresponse_dataIn;

    private long request_dataOut;
    private long offer_dataOut;


    private long accept_dataOut;
    private long data_dataOut;
    private long dataack_dataOut;
    private long cancel_dataOut;
    private long tellme_dataOut;
    private long listresponse_dataOut;


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

        this.request_dataIn = 0;
        this.offer_dataIn = 0;
        this.accept_dataIn = 0;
        this.data_dataIn = 0;
        this.dataack_dataIn = 0;
        this.cancel_dataIn = 0;
        this.tellme_dataIn = 0;
        this.listresponse_dataIn = 0;

        this.request_dataOut = 0;
        this.offer_dataOut = 0;
        this.accept_dataOut = 0;
        this.data_dataOut = 0;
        this.dataack_dataOut = 0;
        this.cancel_dataOut = 0;
        this.tellme_dataOut = 0;
        this.listresponse_dataOut = 0;

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
    public String getTypeData(){
        return ";" + request_dataIn + "/" + request_dataOut + ";" + offer_dataIn + "/" + offer_dataOut + ";" + accept_dataIn + "/" + accept_dataOut + ";" + data_dataIn + "/" + data_dataOut + ";" + dataack_dataIn + "/" + dataack_dataIn + ";" + cancel_dataIn + "/" + cancel_dataOut + ";" + tellme_dataIn + "/" + tellme_dataOut + ";" + listresponse_dataIn + "/" + listresponse_dataOut;
    }

    void overallCounter(boolean in, DataMessage msg, int pieceSize) {

        long size = 0;


        if (in) {
            ingoingTotal++;

            switch (msg.type) {
                case REQUEST:
                    request_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    request_dataIn += size;
                    break;

                case OFFER:
                    offer_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    offer_dataIn += size;
                    break;

                case ACCEPT:
                    accept_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    accept_dataIn += size;
                    break;

                case DATA:
                    data_In++;
                    ingoingData++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber + pieceSize;
                    data_dataIn += size;
                    break;

                case DATAACK:
                    dataack_In++;
                    ingoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    dataack_dataIn += size;
                    break;

                case CANCEL:
                    cancel_In++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    cancel_dataIn += size;
                    break;

                case TELLME:
                    tellme_In++;
                    size = 1;
                    tellme_dataIn += size;
                    break;

                case LISTRESPONSE:
                    listresponse_In++;
                    ingoingData++;

                    size += msg.hash.length() * 3;

                    for (Map.Entry<String, boolean[]> of : msg.offers) {
                        size += of.getKey().length() * 3;
                        size += of.getValue().length * 4;
                    }

                    listresponse_dataIn += size;

                    break;

            }
            dataIn+= size;
        } else {
            outgoingTotal++;

            switch (msg.type) {
                case REQUEST:
                    request_Out++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    request_dataOut += size;
                    break;

                case OFFER:
                    offer_Out++;
                    outgoingData++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    offer_dataOut += size;

                    break;

                case ACCEPT:
                    accept_Out++;
                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    accept_dataOut += size;


                    break;

                case DATA:
                    data_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber + pieceSize;

                    data_dataOut += size;


                    break;

                case DATAACK:
                    dataack_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;
                    dataack_dataOut += size;

                    break;

                case CANCEL:
                    cancel_Out++;
                    outgoingData++;

                    size = 1 + msg.hash.length() * 3 + msg.pieceNumber;

                    cancel_dataOut += size;

                    break;

                case TELLME:
                    tellme_Out++;
                    size = 1;
                    tellme_dataOut += size;
                    break;

                case LISTRESPONSE:
                    listresponse_Out++;
                    outgoingData++;

                    size += msg.hash.length() * 3;
                    for (Map.Entry<String, boolean[]> of : msg.offers) {
                        size += of.getKey().length() * 3;
                        size += of.getValue().length * 4;
                    }
                    listresponse_dataOut += size;
                    break;

            }
            dataOut += size;
        }


    }
}




