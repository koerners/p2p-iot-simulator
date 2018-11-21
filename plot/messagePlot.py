import pandas
import matplotlib.pyplot as plt
import argparse
import os
import math




def plotter(file_seq, path):


    messages = pandas.read_csv(path+"/sentdata_dump"+file_seq+".dat", delimiter=';')

    #print(messages)

    totalOut = messages.iloc[:,1].sum()
    totalIn = messages.iloc[:,2].sum()
    dataOut = messages.iloc[:,3].sum()
    dataIn = messages.iloc[:,4].sum()



















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


    plotter(seqMax,args.path)
