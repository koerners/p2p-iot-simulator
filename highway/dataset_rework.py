import pandas
import pyproj

# example to work
#df = pandas.DataFrame(data={'Vehicle_ID': [1,2,2,2,3,3,3], 'Total_Frames': [11,20,22,22,33,33,35]} )

#df.loc[(df['Vehicle_ID'] == uniques.iloc[2,0]) & (df['Total_Frames'] == uniques.iloc[2,1]), ['Vehicle_ID']] = 1000

def percentage(total, progress):
        percent = (progress / total )*100
	#if (percent.is_integer()):
        print(str(percent)+"%")
                
#needed columns
cols_to_get = ['Vehicle_ID', 'Total_Frames', 'Global_Time', 'Global_X', 'Global_Y', 'Location']

data_in = "highway_101.csv"
#data_in = "shortway.csv"
print("importing file : "+data_in)
df = pandas.read_csv(data_in, usecols=cols_to_get, na_filter=False)


cols_to_get_cell = ['X', 'Y', 'STRUCTYPE']
data_in_cell = "Cellular_Towers.csv"
print("importing file : "+data_in_cell)
df1 = pandas.read_csv(data_in_cell, usecols=cols_to_get_cell, na_filter=False)


print("Isolation of highway 101")
df = df.loc[df['Location'] == 'us-101']

# Celltowers on the west coast
df1 = df1.loc[df1['X'] < -117]
df1 = df1.loc[df1['X'] > -127]

print("Casting from Lat Lon to NAD83")

p1 = pyproj.Proj(proj='latlong',datum='WGS84', preserve_units=True)
p2 = pyproj.Proj(init='epsg:2227', preserve_units=True)

for index, row in df1.iterrows():
    x1 = row["X"]; y1 = row["Y"]
    x2, y2 = pyproj.transform(p1, p2, x1, y1)
    df1.at[index, 'Y'] = x2
    df1.at[index, 'X'] = y2
    #print(x2, y2)


print("Processing duplicates")
uniques = df.drop_duplicates(subset=['Vehicle_ID', 'Total_Frames'])

#TODO: Uncomment again
# print("Rewrite index values")
# for i in range(0, uniques.shape[0]):
#         df.loc[(df['Vehicle_ID'] == uniques.iloc[i,0]) & (df['Total_Frames'] == uniques.iloc[i,1]), ['Vehicle_ID']] = i
#         percentage(uniques.shape[0] ,i)

print("Offsetting timestamps")
minitime = df['Global_Time'].min()
df['Global_Time'] = df['Global_Time'] - minitime


#TODO: Correct offset
minix1 = df['Global_X'].min()
miniy1 = df['Global_Y'].min()
minx2 = df1['X'].min()
miny2 = df1['Y'].min()

minix = min(minix1, minx2)
miniy = min(miniy1, miny2)

#print(minix1, minx2, miniy1, miny2, minix, miniy)
df['Global_X'] = df['Global_X'] - minix
df['Global_Y'] = df['Global_Y'] - miniy

df1['X'] = df1['X'] - minix
df1['Y'] = df1['Y'] - miniy

df1 = df1.loc[df1['X'] > -1]
df1 = df1.loc[df1['Y'] > -1]



print("Sort by ascending time")
df.sort_values('Global_Time', inplace=True)

print("Casting coordinates into integers")
df['Global_X'] = df['Global_X'].astype(int)
df['Global_Y'] = df['Global_Y'].astype(int)

df1['X'] = df1['X'].astype(int)
df1['Y'] = df1['Y'].astype(int)

data_out = "highway_101_fixed.csv"
cols_to_out = ['Global_Time', 'Vehicle_ID', 'Global_X', 'Global_Y']
print("Saving new dataset : "+data_out)
df[cols_to_out].to_csv(data_out, sep=';', index=False)

data_out1 = "cellTowersWestCoast_fixed.csv"
cols_to_out1 = ['X', 'Y', 'STRUCTYPE']
print("Saving new dataset : "+data_out1)
df1[cols_to_out1].to_csv(data_out1, sep=';', index=False)
