from optparse import OptionParser
from networktables import NetworkTable
import datetime
import time

# import colorama
# from colorama import Fore, Back, Style

# colorama.init()

# AUTHOR: Robbie Selwyn

parser = OptionParser()

parser.add_option('-f', '--file',
                  action="store", dest="fname",
                  help="Custom filename.", default="")

options, args = parser.parse_args()

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
        if options.fname == "":
            # Uncomment this if you want files that are named with a timestamp
            #f = open("-".join(str(datetime.datetime.now()).split(" ")).split(".")[0].replace(":","") + ".csv", "w")

            f = open("can-data.csv", "w")
        else:
            f = open(options.fname, "w")

    if table.getString("end", "") == "true" and recording:
        print "Received End"
        recording = False
        f.close()
        exit(0)

    if recording:
        print table.getString("status","")
        f.write(table.getString("status",""))

    time.sleep(.001)
    # print "Wait"