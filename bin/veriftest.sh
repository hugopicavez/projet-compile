#!/bin/bash

for fich in *.ass
do
	echo "------------fichier : $fich-------------------"
	./ima $fich
	echo "----------------------------------------------"
	read r
done 
