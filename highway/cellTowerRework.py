import pandas
from OSGridConverter import latlong2grid
import pyproj
#needed columns
cols_to_get = ['X', 'Y', 'STRUCTYPE']
data_in = "Cellular_Towers.csv"
print("importing file : "+data_in)
df = pandas.read_csv(data_in, usecols=cols_to_get, na_filter=False)

# Celltowers on the west coast
df = df.loc[df['X'] < -117]
df = df.loc[df['X'] > -127]

p1 = pyproj.Proj(proj='latlong',datum='WGS84')
p2 = pyproj.Proj(init='epsg:2227')

print("Casting from Lat Lon to NAD83")
for index, row in df.iterrows():
    x1 = row["X"]; y1 = row["Y"]
    x2, y2 = pyproj.transform(p1, p2, x1, y1)
    df.at[index, 'X'] = x2
    df.at[index, 'Y'] = y2
    print(x2, y2)





# Hardcoded values from the highway dataset (11 jan 2019)
# minix = 6451063.948 #df['X'].min()
# miniy = 1871874.94 #df['Y'].min()
# df['X'] = df['X'] - minix
# df['Y'] = df['Y'] - miniy  

# print("Casting coordinates into integers")
# df['X'] = df['X'].astype(int)
# df['Y'] = df['Y'].astype(int)    




data_out = "cellTowersWestCoast_fixed.csv"
cols_to_out = ['X', 'Y', 'STRUCTYPE']
print("Saving new dataset : "+data_out)
df[cols_to_out].to_csv(data_out, sep=';', index=False)



