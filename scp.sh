#!/usr/bin/env bash

if [[ "$#" -ne 3 ]]; then
  echo -e "\n\nplease provide host,path and version\n\n"
  exit 1
fi


sshpass -p "derschneeman" ssh pi@$1 "mkdir -p $2/$3/"
sshpass -p "derschneeman" ssh pi@$1 "sudo pkill -9 java"
sshpass -p "derschneeman" scp target/lightcontrol.jar pi@$1:$2/$3/lightcontrol.jar
sshpass -p "derschneeman" ssh pi@$1 "sudo reboot &"
