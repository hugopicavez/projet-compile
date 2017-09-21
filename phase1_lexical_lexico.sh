#/bin/sh
ant -f build.xml
cd test/lexico
chmod u+x lexico.sh
./lexico.sh
