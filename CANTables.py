import os
import sys
import time
from networktables import NetworkTables

# 2018 CANTables
# AUTHOR: Robbie Selwyn

GNUPLOT_SCRIPT = """
    set title 'CANTables Plot'
    set xlabel 'Time' # x-axis label
    set ylabel 'Output Value' # y-axis label
    
    # Gnuplot Styling
    set style line 12 lc rgb '#808080' lt 0 lw 1
    set grid back ls 12
    
    set pointsize .5
    
    """

graph = sys.argv[1:]

IP = "roborio-8-frc.local"

NetworkTables.initialize(server=IP)

table = NetworkTables.getTable("data_table")

print table

recording = False
f = None

# All of the keys to get data on
KEYS = ["dt_error_left", "dt_error_right", "dt_velocity_left", "dt_velocity_right", "slider_pot", "slider_enc", "dt_dist"]

time.sleep(1)
print "Started Listening"

while True:
    print table.getString("start","no value")
    
    if table.getString("start", "0") == "true" and not recording:
        print "Received Start"
        recording = True
        f = open("can-data.csv", "w")
    
    if table.getString("end", "0") == "true" and recording:
        print "Received End; Starting CLI"
        recording = False
        f.close()
        break
    
    if recording:
        data = [table.getString(i,"0").replace("\n","") for i in KEYS]
        f.write(",".join(data) + "\n")
    
    time.sleep(0.02)
# print "Wait"

if len(graph) == 0:
    # Assume that the user wants to graph it manually, so notify them of the keys
    print ",".join([str(i) + ": " + v for i,v in enumerate(KEYS)])
    exit(0)


f = open("cantables.gp", "w")
for i in GNUPLOT_SCRIPT.split("\n"):
    f.write(i + "\n")

main_data_string = 'plot '
colors = ["#2ecc71", "#f1c40f", "#8e44ad", "#34495e", "#3498db", "#c0392b", "#1F3A93", "#F22613"]
for i in graph:
    print colors[graph.index(i)]
    main_data_string += "\"can-data.csv\" u " + str(KEYS.index(i)) + \
        " pt 7 lt rgb '" + str(colors[graph.index(i)]) + "' with linespoint title '" + i.replace("_","-") +"', "
f.write(main_data_string + "\n")

f.close()

os.system("cat cantables.gp | gnuplot -p")

