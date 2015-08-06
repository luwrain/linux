#!/bin/sh -e

CLASSPATH=~/base.git/jar/luwrain-base.jar:~/linux.git/jar/luwrain-linux.jar

java -cp "$CLASSPATH" org.luwrain.linux.SysInfoApp "$@"


