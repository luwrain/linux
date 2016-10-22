#!/bin/bash -evx
# Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
# This file is part of the LUWRAIN.

THIS="${0##*/}"

LUWRAIN_USER_DATA_DIR=~/.luwrain
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

if ! [ -d "$LUWRAIN_USER_DATA_DIR/sqlite" ]; then
echo "Preparing initial $LUWRAIN_USER_DATA_DIR/sqlite"
mkdir -p "$LUWRAIN_USER_DATA_DIR/sqlite"
cp -r "$LUWRAIN_HOME/sqlite/." "$LUWRAIN_USER_DATA_DIR/sqlite"
fi

if ! [ -d "$LUWRAIN_USER_DATA_DIR/registry" ]; then
echo "Preparing initial $LUWRAIN_USER_DATA_DIR/registry"
mkdir -p "$LUWRAIN_USER_DATA_DIR/registry"
cp -r "$LUWRAIN_HOME/registry/." "$LUWRAIN_USER_DATA_DIR/registry"
cp -r "$LUWRAIN_HOME/i18n/$LUWRAIN_LANG/." "$LUWRAIN_USER_DATA_DIR/registry"
find "$LUWRAIN_USER_DATA_DIR/registry/org/" -type d -exec touch '{}'/strings.txt \;
find "$LUWRAIN_USER_DATA_DIR/registry/org/" -type d -exec touch '{}'/integers.txt \;
find "$LUWRAIN_USER_DATA_DIR/registry/org/" -type d -exec touch '{}'/booleans.txt \;
fi

mkdir -p "$LUWRAIN_USER_DATA_DIR/extensions"
mkdir -p "$LUWRAIN_USER_DATA_DIR/properties"

LUWRAIN_JAR_DIR="$LUWRAIN_HOME/jar"
LUWRAIN_LIB_DIR="$LUWRAIN_HOME/lib"
LUWRAIN_JNI_DIR="$LUWRAIN_HOME/jni"

CLASS_PATH=":$(jars "$LUWRAIN_LIB_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_JAR_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_USER_DATA_DIR/extensions/.")"

exec java \
-cp "$CLASS_PATH" \
-Djava.library.path="$LUWRAIN_JNI_DIR" \
"$LUWRAIN_MAIN_CLASS" \
--data-dir="$LUWRAIN_HOME/data" \
--user-data-dir="$LUWRAIN_USER_DATA_DIR" \
--lang="$LUWRAIN_LANG" \
"$@" &> "$LUWRAIN_USER_DATA_DIR/log.txt"
