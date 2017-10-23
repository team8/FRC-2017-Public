import time
from networktables import NetworkTable

# import colorama
# from colorama import Fore, Back, Style

# colorama.init()

# AUTHOR: Robbie Selwyn


IP = "roborio-8-frc.local"

NetworkTable.setIPAddress(IP)
NetworkTable.setClientMode()
NetworkTable.initialize()

table = NetworkTable.getTable("data_table")

recording = False
f = None

time.sleep(1)
print "Started Listening"

while True:

    if table.getString("start", "") == "true" and not recording:
        print "Received Start"
        recording = True
        f = open("can-data.csv", "w")

    if table.getString("end", "") == "true" and recording:
        print "Received End"
        recording = False
        f.close()
        exit(0)

    if recording:
        #print table.getString("status","")
        f.write(table.getString("status",""))

    time.sleep(0.02)
    # print "Wait"