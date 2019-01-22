import pandas as pd
import matplotlib.pyplot as plt


# For debugging purposes when using the Python scripts to manipulate the datasets
# Takes the datasets and plots them

df = pd.read_csv('highway_extended.csv', sep=';')
df1 = pd.read_csv('cellTowersWestCoast_fixed.csv', sep=';')


df.plot.scatter(x="Global_X",y="Global_Y", label="Cars")
df1.plot.scatter(x="X",y="Y", label="Antenna")

plt.ylabel('y')
plt.title('Highway')
plt.show()