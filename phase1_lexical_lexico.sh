#/bin/sh
echo -----------------------------------------------------
echo          mettre default="runlex" dans build.xml
echo ----------------------------------------------------------
echo ------Compilation build.xml-------------------
ant -f build.xml
echo ------Exécution src/fr/esisar/compilation/syntaxe/compile.sh-------------------
chmod u+x src/fr/esisar/compilation/syntaxe/compile.sh
cd src/fr/esisar/compilation/syntaxe/
./compile.sh
echo ------Exécution lexico.sh-------------------
cd ../../../../../test/lexico
chmod u+x lexico.sh
./lexico.sh
