#!/bin/bash

# Script shell bash qui permet de lancer les éléments d'une piece
# (Air, Thermometre et Chauffafe) en une seule fois, avec un seul
# passage de parametre.
# Les parametres sont :
# - adrMulticast : Adresse multicast de la piece
# - portMulticast : Port multicast de la piece
# - nomPiece : Le nom de la piece
# - adrSysteme : Adresse du systeme central
# - portSysteme : Port du systeme central

if [ $# -eq 5 ]
then
     set $1 $2 $3 $4 $5
elif [ $# -eq 3 ]
then
     set $1 $2 $3 localhost 12000
elif [ $# -eq 1 ] && [ $1 = "test" ]
then
     set 224.1.2.3 6000 chambre localhost 12000
else
     echo "Usage: piece.sh adrMulticast portMulticast nomPiece"
     exit 1;
fi

gnome-terminal --hide-menubar \
     --tab-with-profile=Default -t "Air" -e "java Air $1 $2 $3" \
     --tab-with-profile=Default -t "Thermometre" -e "java Thermometre $1 $2 $4 $5" \
     --tab-with-profile=Default -t "Chauffage" -e "java Chauffage $1 $2 $3 $4 $5"

#     chaine=''
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
