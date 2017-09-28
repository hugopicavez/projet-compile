#/bin/sh
echo -----------------------------------------------------
echo          mettre default="runsyntax" dans build.xml
echo ----------------------------------------------------------
echo
echo
echo ----------- Build : build.xml
ant -f build.xml
echo -------------------------------------
cd test/syntaxe/
chmod u+x syntaxe.sh
./syntaxe.sh

