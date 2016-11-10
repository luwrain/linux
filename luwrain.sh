#!/bin/bash -e
# Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
# This file is part of the LUWRAIN.

THIS="${0##*/}"
LUWRAIN_MAIN_CLASS=org.luwrain.core.Init

jars()
{
    if [ -d "$1" ]; then
	find "$1" -iname '*.jar' |
	while read l; do
	    echo -n "$l:"
	done
	echo
    fi
}

if [ -z "$LUWRAIN_HOME" ]; then
    if [ -e ~/luwrain/luwrain.sh ]; then
	LUWRAIN_HOME=~/luwrain
    elif [ -e /opt/luwrain/luwrain.sh ]; then
	LUWRAIN_HOME=/opt/luwrain
    else
	echo "$THIS:unable to find the directory with LUWRAIN distribution (must be ~/luwrain or /opt/luwrain)" >&2
	exit 1
    fi
fi

if [ -z "$LUWRAIN_LANG" ]; then
    LUWRAIN_LANG="${LANG%%_*}"
fi

if ! [ -e "$LUWRAIN_HOME/i18n/$LUWRAIN_LANG" ]; then
    LUWRAIN_LANG=en
fi

LUWRAIN_JAR_DIR="$LUWRAIN_HOME/jar"
LUWRAIN_LIB_DIR="$LUWRAIN_HOME/lib"
CLASS_PATH=":$(jars "$LUWRAIN_LIB_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_JAR_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_USER_DATA_DIR/extensions/.")"

cd "$LUWRAIN_HOME"
exec java \
-cp "$CLASS_PATH" \
-Djava.library.path="$LUWRAIN_HOME" \
"$LUWRAIN_MAIN_CLASS" \
"$@" &> ~/.luwrain.log
