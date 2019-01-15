import pandas as pd
import numpy as np


def normalize(list): 
  l = np.array(list) 
  a = np.max(l)
  c = np.min(l)
  b = 0
  d = 1000

  m = (b - d) / (a - c)
  pslope = (m * (l - c)) + d
  return pslope

df = pd.read_csv('highway_fixed_2.csv', sep=';')

veh = df.Vehicle_ID.unique()

j = 0

list = []

print("Creating new data")
for car in veh:
    j+=1
    print((j/veh.size)*100)

    loca = df.loc[df['Vehicle_ID'] == car]
    dfT = loca['Global_Time'][loca.index[-1]]
    dfX = loca['Global_X'][loca.index[-1]]
    dfY = loca['Global_Y'][loca.index[-1]]

    for i in range(187):

        entry = pd.Series([dfT , car , dfX , dfY ],  index=df.columns )
        list.append(entry)
        dfT += 100
        dfX += 3
        dfY += 3

print("Appending new data")
modDfObj = df.append(list , ignore_index=True)

print("Sort by ascending time")
modDfObj.sort_values('Global_Time', inplace=True)

print("Normalizing")

modDfObj['Global_X'] = normalize(modDfObj['Global_X'])
modDfObj['Global_Y'] = normalize(modDfObj['Global_Y'])

modDfObj['Global_X'] = modDfObj['Global_X'].astype(int)
modDfObj['Global_Y'] = modDfObj['Global_Y'].astype(int)
modDfObj['Vehicle_ID'] = modDfObj['Vehicle_ID'].astype(int)
modDfObj['Global_Time'] = modDfObj['Global_Time'].astype(int)

print(modDfObj)

data_out = "highway_extended.csv"
cols_to_out = ['Global_Time', 'Vehicle_ID', 'Global_X', 'Global_Y']
print("Saving new dataset : "+data_out)
modDfObj[cols_to_out].to_csv(data_out, sep=';', index=False)


