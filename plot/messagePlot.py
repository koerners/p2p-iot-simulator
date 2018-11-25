import pandas
import matplotlib.pyplot as plt
import numpy as np
import argparse
import os

def plotterVar(file_seq, path):
    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    tOvar=messages.loc[0:,'totalOut'].var()
    tIvar=messages.loc[0:,'totalIn'].var()
    dOvar=messages.loc[0:,'dataOut'].var()
    dIvar=messages.loc[0:,'dataIn'].var()

    N = 1
    totalOut_means = (tOvar)

    ind = np.arange(N)  # the x locations for the groups
    width = 0.5     # the width of the bars

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
    ax.set_xticklabels(('varianz'))

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
    ax.set_xticks(ind + width / 4)
    ax.set_xticklabels(('min', 'max', 'avg'))

    ax.legend((rects1[0], rects2[0], rects3[0], rects4[0]), ('TotalOut', 'TotalIn','DataOut','DataIn'))

    plt.savefig("AvgNumbers"'.png', dpi = (200))


    def autolabel(rects):
        """
        Attach a text label above each bar displaying its height
        """
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/4., 1.2*height,
                    '%d' % int(height),
                    ha='center', va='bottom')

    autolabel(rects1)
    autolabel(rects2)
    autolabel(rects3)
    autolabel(rects4)

    # plt.show()




def plotterTotal(file_seq, path):


    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    print(messages)

    totalOut = messages.iloc[:,1].sum()
    totalIn = messages.iloc[:,2].sum()
    dataOut = messages.iloc[:,3].sum()
    dataIn = messages.iloc[:,4].sum()

    std_tO=messages.loc[0:,'totalOut'].std()
    std_tI=messages.loc[0:,'totalIn'].std()
    std_dO=messages.loc[0:,'dataOut'].std()
    std_dI=messages.loc[0:,'dataIn'].std()

    N = 2
    out_numb = (totalOut, dataOut)
    out_std = (std_tO,std_dO)

    ind = np.arange(N)  # the x locations for the groups
    width = 0.35       # the width of the bars

    fig, ax = plt.subplots()
    rects1 = ax.bar(ind, out_numb, width, color='r', yerr=out_std)

    in_numb = (totalIn, dataIn)
    in_std = (std_tI,std_dI)
    rects2 = ax.bar(ind + width, in_numb, width, color='y', yerr=in_std)

    # add some text for labels, title and axes ticks
    ax.set_ylabel('Messages')
    ax.set_title('Total')
    ax.set_xticks(ind + width / 2)
    ax.set_xticklabels(('totalOut/In', 'dataOut/In'))

    ax.legend((rects1[0], rects2[0]), ('Out', 'In'))

    plt.savefig("totalNumbers"'.png', dpi = (200))




    def autolabel(rects):
        """
        Attach a text label above each bar displaying its height
        """
        for rect in rects:
            height = rect.get_height()
            ax.text(rect.get_x() + rect.get_width()/2., 1.2*height,
                    '%d' % int(height),
                    ha='center', va='bottom')

    autolabel(rects1)
    autolabel(rects2)

    #plt.show()

    print(totalOut)
    print (totalIn)
    print (dataIn)
    print (dataOut)




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
