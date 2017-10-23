import matplotlib.pyplot as plt
import sys

assert len(sys.argv) > 1

path_to_output = sys.argv[1]

FILE_SEPERATOR = " " # The default is space separated values.  This can be changed to comma seperated

f = open(path_to_output, "r").readlines()

data = [i.split(FILE_SEPERATOR) for i in f[2:]]

plt.title("Graph of {}".format(path_to_output.split("/")[-1].split(".")[0]))
plt.grid(True)

if "Red" in path_to_output:
	color='r'
else:
	color='b'

plt.axis('equal')
plt.xlabel("X Position")
plt.ylabel("Y Position")

data_y = [float(i[6]) for i in data]
data_x = [-float(i[7]) for i in data]

plt.axhline(0, color=color)
plt.axvline(0, color=color)
plt.scatter(data_x, data_y, color=color)
plt.show()
