import matplotlib.pyplot as plt
import sys

assert len(sys.argv) > 1

path_to_output = sys.argv[1]
# index = int(sys.argv[2])

FILE_SEPARATOR = "," # The default is space separated values.  This can be changed to comma seperated

f = open(path_to_output, "r").readlines()

data = [i.split(FILE_SEPARATOR) for i in f]

plt.title("Graph of {}".format(path_to_output.split("/")[-1].split(".")[0]))
plt.grid(True)

plt.xlabel("X Position")
plt.ylabel("Y Position")

# 0 is error, 1 is output, 2 is distance so far, 3 is calculated velocity, 4 desired velocity, 5 desired acceleration
data_y = [i[0] for i in data]
data_x = [i for i in range(len(data_y))]
# desired degrees, supposed heading, calculated error

data_y_2 = [i[2] for i in data]
data_x_2 = [i for i in range(len(data_y_2))]


data_y_3 = [i[2] for i in data]
data_x_3 = [i for i in range(len(data_y_3))]

data_y_4 = [i[6] for i in data]
data_x_4 = [i for i in range(len(data_y_4))]

data_y_5 = [i[5] for i in data]
data_x_5 = [i for i in range(len(data_y_5))]

# plt.axis('equal')
#plt.scatter(data_x, data_y, color="b")
plt.scatter(data_x_2, data_y_2, color='r')
#plt.scatter(data_x_3, data_y_3, color='g')
plt.scatter(data_x_4, data_y_4, color='yellow')
plt.scatter(data_x_5, data_y_5, color='purple')
plt.show()
