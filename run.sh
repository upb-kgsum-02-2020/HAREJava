mvn clean install
 nohup mvn -e exec:java -Dexec.mainClass="org.aksw.dice.main.TimeEvaluation" -Dexec.args="-f ../dataset/dogfood.nt -t hare" &
