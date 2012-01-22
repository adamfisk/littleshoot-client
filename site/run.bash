#!/usr/bin/env bash
function die() {
  echo $*
  exit 1
}

mvn package -Dmaven.test.skip=true || die "Could not package"

fullPath=`dirname $0`
jar=`find $fullPath/target/*with-dependencies.jar`
cp=`echo $jar | sed 's,./,'$fullPath'/,'`
#javaArgs="-server -Xmx600m -jar "$cp" $*"

javaSystemProps="\
-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger \
-Dorg.mortbay.xml.XmlParser.NotValidating=true \
-Djava.net.preferIPv4Stack=true \
-Djava.net.preferIPv6Addresses=false \

"

javaArgs="-Xmx200m -jar "$cp" $*"

echo "Running using Java on path at `which java` with args $javaArgs"
java -Dorg.lastbamboo.client.version=0.00 $javaSystemProps $javaArgs launchd $@ || die "Java process exited abnormally"


#echo "Running using Java on path at `which java` with args $javaArgs"
#java $javaArgs || die "Java process exited abnormally"
