#!/bin/sh
echo ------Compilation build.xml-------------------
ant -f build.xml
echo ------Exécution src/fr/esisar/compilation/syntaxe/compile.sh-------------------
chmod u+x src/fr/esisar/compilation/syntaxe/compile.sh
cd src/fr/esisar/compilation/syntaxe/
./compile.sh
