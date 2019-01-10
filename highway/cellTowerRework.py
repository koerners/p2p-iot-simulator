import pandas
from OSGridConverter import latlong2grid

#needed columns
cols_to_get = ['X', 'Y', 'STRUCTYPE']
data_in = "Cellular_Towers.csv"
print("importing file : "+data_in)
df = pandas.read_csv(data_in, usecols=cols_to_get, na_filter=False)

# Celltowers on the west coast
locx = df.loc[df['X'] < -117]


minix = df['X'].min()
miniy = df['Y'].min()
df['X'] = df['X'] - minix
df['Y'] = df['Y'] - miniy


print("Casting coordinates into integers")
df['X'] = df['X'].astype(int)
df['Y'] = df['Y'].astype(int)

#Casting from Lat Lon to NAD83

for index, row in df.iterrows():
    g = latlong2grid(row["X"], row["Y"], tag = 'NAD83')
    #print(g.E,g.N)
    df.at[index, 'X'] = g.E
    df.at[index, 'Y'] = g.N

data_out = "cellTowersWestCoast_fixed.csv"
cols_to_out = ['X', 'Y', 'STRUCTYPE']
print("Saving new dataset : "+data_out)
df[cols_to_out].to_csv(data_out, sep=';', index=False)



