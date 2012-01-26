#!/bin/bash
#
# ------------------------------------------------------
# Bamboo Startup Script for Unix
# ------------------------------------------------------

#
# Check correct command line usage
#
usage()
{
    echo "Usage: $0 {start|stop|restart|status}"
    exit 1
}

[ $# -gt 0 ] || usage

#
# Get the action & configs
#
ACTION=$1

#
# Ensure the BAMBOO_INSTALL var for this script points to the
# home directory where Bamboo is is installed on your system.
#

BAMBOO_INSTALL=.

export JAVA_HOME
export BAMBOO_INSTALL

#
# Find a PID for the pid file
#
if [  -z "$BAMBOO_PID" ]
then
    BAMBOO_PID="$BAMBOO_INSTALL/bamboo.pid"
fi

#
# Find a location for the bamboo console
#
if [  -z "$BAMBOO_CONSOLE" ]
then
  if [ -w /dev/console ]
  then
    BAMBOO_CONSOLE=/dev/console
  else
    BAMBOO_CONSOLE=/dev/tty
  fi
fi

#
# Are we running on Windows? Could be, with Cygwin/NT.
#
case "`uname`" in
CYGWIN*) PATH_SEPARATOR=";";;
*) PATH_SEPARATOR=":";;
esac

#
# Save the current CLASSPATH
#
OLDCLASSPATH=$CLASSPATH

#
# Put the javac compiler in the classpath;
#
#CLASSPATH=$JAVA_HOME/lib/tools.jar

#
# Add the bamboo classes in the classpath
#
CLASSPATH=$CLASSPATH$PATH_SEPARATOR$BAMBOO_INSTALL/webapp/WEB-INF/classes/

#
# Now add in the bamboo library jars
#
CLASSPATH=$CLASSPATH$PATH_SEPARATOR$(ls $BAMBOO_INSTALL/webapp/WEB-INF/lib/*.jar | paste -s -d"$PATH_SEPARATOR" - )

#
# Append old classpath to current classpath
#
if [ ! -z "$OLDCLASSPATH" ]; then
    CLASSPATH=${CLASSPATH}:$OLDCLASSPATH
fi

#
# This is how the Bamboo server will be started
#
#RUN_CMD="java -Xms256m -Xmx512m -XX:MaxPermSize=256m -Djava.awt.headless=true -classpath $CLASSPATH -Dorg.mortbay.xml.XmlParser.NotValidating=true -Djetty.port=8085 com.atlassian.bamboo.server.Server 8085 ./webapp /"

RUN_CMD="java -Xms256m -Xmx512m -XX:MaxPermSize=256m -Djava.awt.headless=true -classpath $CLASSPATH -Dorg.mortbay.xml.XmlParser.NotValidating=true com.atlassian.bamboo.server.Server webapp/WEB-INF/classes/jetty.xml"

#
# Do the action
#
case "$ACTION" in
  start)
        echo "Starting Bamboo: "

        if [ -f $BAMBOO_PID ]
        then
            echo "Already Running!!"
            exit 1
        fi

        echo "STARTED Bamboo `date`" >> $BAMBOO_CONSOLE

        cd $BAMBOO_INSTALL
        nohup sh -c "exec $RUN_CMD 2>&1" >$BAMBOO_INSTALL/logs/bamboo.log &
        echo $! > $BAMBOO_PID
        echo "Bamboo running pid="`cat $BAMBOO_PID`
        ;;

  console)
        echo "Starting Bamboo: "

        if [ -f $BAMBOO_PID ]
        then
            echo "Already Running!!"
            exit 1
        fi

        echo "STARTED Bamboo `date`" >> $BAMBOO_CONSOLE

        cd $BAMBOO_INSTALL
        eval $RUN_CMD
        ;;
  
  stop)
        PID=`cat $BAMBOO_PID 2>/dev/null`
        echo "Shutting down Bamboo: $PID"
        kill $PID 2>/dev/null
        sleep 2
        kill -9 $PID 2>/dev/null
        rm -f $BAMBOO_PID
        echo "STOPPED `date`" >>$BAMBOO_CONSOLE
        ;;

  restart)
        $0 stop $*
        sleep 5
        $0 start $*
        ;;

  status)
        if [ -f $BAMBOO_INSTALL/bamboo.pid ]
        then
            echo "Bamboo running pid="`cat $BAMBOO_INSTALL/bamboo.pid`
            exit 0
        else
            echo "Bamboo is currently not running."
        fi
        exit 1
        ;;

*)
        usage
        ;;
esac

exit 0
