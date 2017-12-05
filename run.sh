mvn clean install
 nohup mvn -e exec:java -Dexec.mainClass="org.aksw.dice.main.ResultsWriteHandler" -Dexec.args="-f ../airports.nt"  > /tmp/hare.log 2>&1 &
 taskset -cp 0,32
