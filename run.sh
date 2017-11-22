mvn clean install
mvn -e exec:java -Dexec.mainClass="org.aksw.dice.main.Example" -Dexec.args="-f data.ttl -t hare"
