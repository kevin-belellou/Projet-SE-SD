#!/bin/bash

chaine=''

if [ $# -ge 3 ]
then
#     while getopts c option
#     do
#          case $option in
#          c)   gnome-terminal --hide-menubar --window-with-profile=Polo -t "Module Communication" -e "./moduleComm.out 12000"
#               #chaine="gnome-terminal --tab-with-profile=Default -t \"Module Communication\" -e \"moduleComm.out 12000\""
#               shift
#          ;;
#          esac
#     done

#     getopts c option
#     if [ "$option" = "c" ]
#     then
#          echo "lol"
#          gnome-terminal --hide-menubar -t "Module Communication" -e "./moduleComm.out 12000"
#     fi

     gnome-terminal --hide-menubar \
          --tab-with-profile=Default -t "Air" -e "java Air $1 $2 $3" \
          --tab-with-profile=Default -t "Thermometre" -e "java Thermometre $1 $2" \
          --tab-with-profile=Default -t "Chauffage" -e "java Chauffage $1 $2 $3"
else
     echo "Usage: piece.sh adrMulticast portMulticast nomPiece"
fi
