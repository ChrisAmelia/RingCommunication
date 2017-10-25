#!/usr/bin/python3

import argparse, os.path, subprocess

parser = argparse.ArgumentParser()

parser.add_argument("tcp", type=int, nargs='?', help="Local TCP port")
parser.add_argument("udp", type=int, nargs='?', help="Local UDP port")

args = parser.parse_args()

if not os.path.isfile("Main.class"):
    subprocess.call("make")

if (args.tcp == None) and (args.udp == None):
    subprocess.call(["/usr/bin/java", "Main"])
else:
    subprocess.call(["/usr/bin/java", "Main", str(args.tcp), str(args.udp)])
