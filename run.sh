mvn clean install
 nohup mvn -e exec:java -Dexec.mainClass="org.aksw.dice.main.TimeEvaluation" -Dexec.args="-f ../dataset/sec.nt -t hare"  > /tmp/hare.log 2>&1 &
