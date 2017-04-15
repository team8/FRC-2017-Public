import sys
import matplotlib.pyplot as plt

assert len(sys.argv) > 1

path_to_output = sys.argv[1]

FILE_SEPARATOR = "," # The default is space separated values.  This can be changed to comma seperated

f = open(path_to_output, "r").readlines()

data = [i.split(FILE_SEPARATOR) for i in f]

plt.title("Graph of {}".format(path_to_output.split("/")[-1].split(".")[0]))
plt.grid(True)

plt.xlabel("X Position")
plt.ylabel("Y Position")

# 0 is error, 1 is output, 2 is velocity
data_y = [i[1] for i in data]
data_x = [i for i in range(len(data_y))]
# plt.axis('equal')
plt.scatter(data_x, data_y, color="b")
plt.show()