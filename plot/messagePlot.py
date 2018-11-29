import pandas
import matplotlib.pyplot as plt
import numpy as np
import argparse
import os
import operator

def plotterVar(file_seq, path):
    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    tOvar=messages.loc[0:,'totalOut'].var()
    tIvar=messages.loc[0:,'totalIn'].var()
    dOvar=messages.loc[0:,'dataOut'].var()
    dIvar=messages.loc[0:,'dataIn'].var()

    N = 1
    totalOut_means = (tOvar)

    ind = np.arange(N)  # the x locations for the groups
    width = 0.4     # the width of the bars

    fig, ax = plt.subplots()
    rects1 = ax.bar(ind, totalOut_means, width, color='r')

    totalIn_means = (tIvar)
    rects2 = ax.bar(ind + width, totalIn_means, width, color='y')

    dataOut_means = (dOvar)
    rects3 = ax.bar(ind + 2*width, dataOut_means, width, color='b')

    dataIn_means = (dIvar)
    rects4 = ax.bar(ind + 3*width, dataIn_means, width, color='m')


    # add some text for labels, title and axes ticks
    ax.set_ylabel('messages')
    ax.set_title('variance')
    ax.set_xticks(ind + width / 4)

    ax.legend((rects1[0], rects2[0], rects3[0], rects4[0]), ('TotalOut', 'TotalIn','DataOut','DataIn'))

    plt.savefig("Variance"'.png', dpi = (200))




def plotterAvg(file_seq, path):

    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')


    tOMin=messages.loc[0:,'totalOut'].min()
    tOMax=messages.loc[0:,'totalOut'].max()
    tOavg = messages.loc[0:,'totalOut'].mean()

    tIMin=messages.loc[0:,'totalIn'].min()
    tIMax=messages.loc[0:,'totalIn'].max()
    tIavg = messages.loc[0:,'totalIn'].mean()

    dOMin=messages.loc[0:,'dataOut'].min()
    dOMax=messages.loc[0:,'dataOut'].max()
    dOavg = messages.loc[0:,'dataOut'].mean()

    dIMin=messages.loc[0:,'dataIn'].min()
    dIMax=messages.loc[0:,'dataIn'].max()
    dIavg = messages.loc[0:,'dataIn'].mean()

    N = 3

    ind = np.arange(N)  # the x locations for the groups
    width = 0.2     # the width of the bars

    fig, ax = plt.subplots()
    totalOut_means = (tOMin,tOMax,tOavg)
    rects1 = ax.bar(ind, totalOut_means, width, color='r')

    totalIn_means = (tIMin,tIMax,tIavg)
    rects2 = ax.bar(ind + width, totalIn_means, width, color='y')

    dataOut_means = (dOMin,dOMax,dOavg)
    rects3 = ax.bar(ind + 2*width, dataOut_means, width, color='b')

    dataIn_means = (dIMin,dIMax,dIavg)
    rects4 = ax.bar(ind + 3*width, dataIn_means, width, color='m')


    # add some text for labels, title and axes ticks
    ax.set_ylabel('Messages')
    ax.set_title('AvgNumbers')
    ax.set_xticks(ind + width / 3)
    ax.set_xticklabels(('min', 'max', 'avg'))

    ax.legend((rects1[0], rects2[0], rects3[0], rects4[0]), ('TotalOut', 'TotalIn','DataOut','DataIn'))

    plt.savefig("AvgNumbers"'.png', dpi = (200))

    """""
    def autolabel(rects):
        Attach a text label above each bar displaying its height
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/4., 1.2*height,
                    '%d' % int(height),
                    ha='center', va='bottom')

    autolabel(rects1)
    autolabel(rects2)
    autolabel(rects3)
    autolabel(rects4)
    """

def splitSum(msg):
    sumIn  =0
    sumOut =0
    for all in msg:
        a,b=all.split("/")
        print(a,b)
        sumIn= sumIn+int(a)
        sumOut= sumOut+int(b)
    print ("--")
    print (sumIn,sumOut)
    return(sumIn,sumOut)

def plotterData(file_seq, path):

    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    dataOut = messages.iloc[:,3].sum()
    dataIn = messages.iloc[:,4].sum()

    std_dO=messages.loc[0:,'dataOut'].std()
    std_dI=messages.loc[0:,'dataIn'].std()

    mpl_fig = plt.figure()
    ax = mpl_fig.add_subplot(111)
    N = 1
    ind = np.arange(N)  # the x locations for the groups
    width = 0.35       # the width of the bars


    rects1 = ax.bar(ind, dataOut, width, color='g', yerr=std_dO)
    rects2 = ax.bar(ind + width, dataIn, width, color='y', yerr=std_dI)

    ax.set_ylabel('data')
    ax.set_title('Data')
    ax.set_xticks(ind + width / 2.)

    ax.legend((rects1[0], rects2[0]), ('DataOut','DataIn'))
    plt.savefig("Data"'.png', dpi = (200))


def plotterTotal(file_seq, path):


    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    print(messages)

    requestIn, requestOut = splitSum(messages.iloc[:,7])
    request=np.array([requestOut, requestIn])

    offerIn, offerOut = splitSum(messages.iloc[:,8])
    offer= np.array([offerOut,offerIn])

    acceptIn, acceptOut = splitSum(messages.iloc[:,9])
    accept=np.array([acceptOut, acceptIn])

    datamIn, datamOut = splitSum(messages.iloc[:,10])
    datam=np.array([datamOut,datamIn])

    dataAckIn, dataAckOut = splitSum(messages.iloc[:,11])
    dataAck= np.array([dataAckOut,dataAckIn])

    CancelIn, CancelOut = splitSum(messages.iloc[:,12])
    Cancel=np.array([CancelOut,CancelIn])

    TellMeIn, TellMeOut = splitSum(messages.iloc[:,13])
    TellMe=np.array([TellMeOut,TellMeIn])

    ListResponseIn, ListResponseOut = splitSum(messages.iloc[:,14])
    ListResponse=np.array([ListResponseOut,ListResponseIn])

    print(request, accept,Cancel)


    std_tO=messages.loc[0:,'totalOut'].std()
    std_tI=messages.loc[0:,'totalIn'].std()

    std = (std_tO,std_tI)


    N = 2


    ind = np.arange(N)  # the x locations for the groups
    width = 0.4       # the width of the bars
    fig, ax = plt.subplots()
    print ("............")
    print (request,offer,accept)


    p1 = ax.bar(ind, request, width, color='r')
    p2 = ax.bar(ind, offer, width,bottom=request, color='g')
    p3 = ax.bar(ind, accept, width,bottom=request+offer, color=(0.5,1.0,0.62))
    p4 = ax.bar(ind, datam, width, bottom=request+offer+accept, color='c')
    p5 = ax.bar(ind, dataAck, width,bottom=request+offer+accept+datam, color='m')
    p6 = ax.bar(ind, TellMe, width,bottom=request+offer+accept+datam+dataAck, color='b')
    p7 = ax.bar(ind, Cancel, width,bottom=request+offer+accept+datam+dataAck+TellMe, color='y')
    p8 = ax.bar(ind, ListResponse, width,bottom=request+offer+accept+datam+dataAck+TellMe+Cancel, color=(1.0,0.7,0.62))

    # add some text for labels, title and axes ticks
    ax.set_ylabel('Messages')
    ax.set_title('Total')
    ax.set_xticks(ind+width/2)
    ax.set_xticklabels(('totalOut', 'TotalIn'))

    ax.legend((p1[0], p2[0], p3[0], p4[0], p5[0], p6[0], p7[0], p8[0]),
              ('Request', 'Offer','Accept','DataM','DataAck','TellMe','Cancel','ListResponse'))

    plt.savefig("totalNumbers"'.png', dpi = (200))



    """""
    def autolabel(rects):
        
        Attach a text label above each bar displaying its height
        
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/2., 1.2*height,
                    '%d' % int(height),
                    ha='center', va='bottom')

    autolabel(p1)
    autolabel(p2)
    autolabel(p3)
    autolabel(p4)
    autolabel(p5)
    autolabel(p6)
    autolabel(p7)
    autolabel(p8)
    """


if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='turn datafile into 2d map of nodes')
    parser.add_argument('path', type=str, help='path to .dat files')
    args = parser.parse_args()

    # get list of files
    files = []
    for dat in os.listdir(args.path):
        if dat.startswith("sentdata") and dat.endswith(".dat"):
            files.append(dat.lstrip("sentdata_dump").rstrip(".dat"))



    seqMax = sorted(files)[-1]


    plotterTotal(seqMax,args.path)
    plotterAvg(seqMax,args.path)
    plotterVar(seqMax,args.path)
    plotterData(seqMax,args.path)
