import pandas
from OSGridConverter import latlong2grid
import pyproj
#needed columns
cols_to_get_cell = ['X', 'Y', 'STRUCTYPE']
data_in_cell = "Cellular_Towers.csv"
print("importing file : "+data_in_cell)
df1 = pandas.read_csv(data_in_cell, usecols=cols_to_get_cell, na_filter=False)

# Celltowers on the west coast

print("Casting from Lat Lon to NAD83")

p1 = pyproj.Proj(proj='latlong',datum='WGS84', preserve_units=True)
p2 = pyproj.Proj(init='epsg:2227', preserve_units=True)


for index, row in df1.iterrows():
    x1 = row["X"]; y1 = row["Y"]
    x2, y2 = pyproj.transform(p1, p2, x1, y1)
    df1.at[index, 'X'] = x2
    df1.at[index, 'Y'] = y2
    #print(x2, y2)




# Hardcoded values from the highway dataset (11 jan 2019)
minix = 6451063.948 #df['X'].min()
miniy = 1871874.94 #df['Y'].min()
maxX = 6452740.916
maxY = 1873411.659




df1 = df1.loc[df1['X'] < maxX*10]
df1 = df1.loc[df1['Y'] < maxY*10]


df1['X'] = df1['X'] - minix
df1['Y'] = df1['Y'] - miniy

df1 = df1.loc[df1['X'] > -1]
df1 = df1.loc[df1['Y'] > -1]


df1['X'] = df1['X'].astype(int)
df1['Y'] = df1['Y'].astype(int)

data_out1 = "cellTowersWestCoast_fixed.csv"
cols_to_out1 = ['X', 'Y', 'STRUCTYPE']
print("Saving new dataset : "+data_out1)
df1[cols_to_out1].to_csv(data_out1, sep=';', index=False)



