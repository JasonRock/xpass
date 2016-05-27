sbt -mem 256 dist
cp target/universal/xpass-1.0-SNAPSHOT.zip ../
cd ..
rm -r xpass-1.0-SNAPSHOT
unzip xpass-1.0-SNAPSHOT.zip