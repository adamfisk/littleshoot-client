#!/bin/sh

javaArgs="-XX:+HeapDumpOnOutOfMemoryError "

classpath="\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-client-site/1.0/lastbamboo-client-site-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-client-launcher-all/1.0/lastbamboo-client-launcher-all-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-client-services/1.0/lastbamboo-client-services-1.0.jar:\
/Users/afisk/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-servlet/7.3.1.v20110307/jetty-servlet-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-security/7.3.1.v20110307/jetty-security-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-server/7.3.1.v20110307/jetty-server-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-continuation/7.3.1.v20110307/jetty-continuation-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-http/7.3.1.v20110307/jetty-http-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-io/7.3.1.v20110307/jetty-io-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/org/eclipse/jetty/jetty-util/7.3.1.v20110307/jetty-util-7.3.1.v20110307.jar:\
/Users/afisk/.m2/repository/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-client-prefs/1.0/lastbamboo-client-prefs-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-client-resource/1.0/lastbamboo-client-resource-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-bencode/1.0/lastbamboo-common-bencode-1.0.jar:\
/Users/afisk/.m2/repository/org/littleshoot/littleshoot-downloader/1.0-SNAPSHOT/littleshoot-downloader-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/http-client/1.0-SNAPSHOT/http-client-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-jlibtorrent/1.0/lastbamboo-common-jlibtorrent-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-npapi/1.0/lastbamboo-common-npapi-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-rest/1.0/lastbamboo-common-rest-1.0.jar:\
/Users/afisk/.m2/repository/commons-id/commons-id/0.1/commons-id-0.1.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-json/1.0/lastbamboo-common-json-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-flickr/1.0/lastbamboo-common-searchers-flickr-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-isohunt/1.0/lastbamboo-common-searchers-isohunt-1.0.jar:\
/Users/afisk/.m2/repository/commons-discovery/commons-discovery/0.2/commons-discovery-0.2.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-littleshoot/1.0/lastbamboo-common-searchers-littleshoot-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-yahoo/1.0/lastbamboo-common-searchers-yahoo-1.0.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-youtube/1.0/lastbamboo-common-searchers-youtube-1.0.jar:\
/Users/afisk/.m2/repository/org/littleshoot/sip-stack/1.0-SNAPSHOT/sip-stack-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/commons-collections/commons-collections/3.2/commons-collections-3.2.jar:\
/Users/afisk/.m2/repository/org/littleshoot/mina-util/1.0-SNAPSHOT/mina-util-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/offer-answer/1.0-SNAPSHOT/offer-answer-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/stun-stack/1.0-SNAPSHOT/stun-stack-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/littleshoot-commons-id/1.0-SNAPSHOT/littleshoot-commons-id-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/jlibtorrent/1.0-SNAPSHOT/jlibtorrent-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/portmapping/1.0-SNAPSHOT/portmapping-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/limewire/libtorrent/5.4.8-SNAPSHOT/libtorrent-5.4.8-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-log4j-bug-appender/1.0/lastbamboo-common-log4j-bug-appender-1.0.jar:\
/Users/afisk/.m2/repository/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar:\
/Users/afisk/.m2/repository/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar:\
/Users/afisk/.m2/repository/commons-io/commons-io/2.0.1/commons-io-2.0.1.jar:\
/Users/afisk/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar:\
/Users/afisk/.m2/repository/org/lastbamboo/lastbamboo-common-searchers-limewire/1.0/lastbamboo-common-searchers-limewire-1.0.jar:\
/Users/afisk/.m2/repository/org/littleshoot/util/1.0-SNAPSHOT/util-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/commons-codec/commons-codec/1.5/commons-codec-1.5.jar:\
/Users/afisk/.m2/repository/org/littleshoot/p2p/1.0-SNAPSHOT/p2p-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/com/barchart/udt/barchart-udt4-bundle/1.0.3-SNAPSHOT/barchart-udt4-bundle-1.0.3-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/littleshoot-ice/1.0-SNAPSHOT/littleshoot-ice-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/sdp/1.0-SNAPSHOT/sdp-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/stun-client/1.0-SNAPSHOT/stun-client-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/stun-server/1.0-SNAPSHOT/stun-server-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/tcp-framing/1.0-SNAPSHOT/tcp-framing-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/turn-http-server/1.0-SNAPSHOT/turn-http-server-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/mina-port/1.0/mina-port-1.0.jar:\
/Users/afisk/.m2/repository/org/littleshoot/sip-bootstrap/1.0-SNAPSHOT/sip-bootstrap-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/p2p-sockets/1.0-SNAPSHOT/p2p-sockets-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/sip-client/1.0-SNAPSHOT/sip-client-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/sip-http-client/1.0-SNAPSHOT/sip-http-client-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/turn-client/1.0-SNAPSHOT/turn-client-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/dnssec4j/0.1-SNAPSHOT/dnssec4j-0.1-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/dnsjava/2.1.3-SNAPSHOT/dnsjava-2.1.3-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/udt/1.0-SNAPSHOT/udt-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/xmpp/1.0-SNAPSHOT/xmpp-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/org/littleshoot/smack-xmpp-3-2-2/1.0-SNAPSHOT/smack-xmpp-3-2-2-1.0-SNAPSHOT.jar:\
/Users/afisk/.m2/repository/com/jcraft/jzlib/1.0.7/jzlib-1.0.7.jar:\
/Users/afisk/.m2/repository/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar:\
/Users/afisk/.m2/repository/com/google/guava/guava/r09/guava-r09.jar:\
/Users/afisk/.m2/repository/log4j/log4j/1.2.14/log4j-1.2.14.jar:\
/Users/afisk/.m2/repository/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar:\
/Users/afisk/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar:\

"

javaSystemProps="\
-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger \
-Dorg.mortbay.xml.XmlParser.NotValidating=true \
-Djava.net.preferIPv4Stack=true \
-Djava.net.preferIPv6Addresses=false \

"

java -Dorg.lastbamboo.client.version=0.00 $javaArgs $javaSystemProps -classpath $classpath org.lastbamboo.client.launcher.all.Launcher launchd $@
