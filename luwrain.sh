#!/bin/bash -e
# Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
#
# This file is part of the LUWRAIN.
#
# LUWRAIN is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# LUWRAIN is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.

THIS="${0##*/}"

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
	echo "$THIS:unable to find the directory with LUWRAIN files" >&2
	exit 1
    fi
fi

if [ -z "$LUWRAIN_LANG" ]; then
    LUWRAIN_LANG=en
fi

LUWRAIN_MAIN_CLASS=org.luwrain.core.Init
LUWRAIN_USER_HOME_DIR=~
LUWRAIN_EXT_DIR=~/.luwrain/extensions
LUWRAIN_REGISTRY_DIR=~/.luwrain/registry
LUWRAIN_DATA_DIR="$LUWRAIN_HOME/data"
LUWRAIN_JAR_DIR="$LUWRAIN_HOME/jar"
LUWRAIN_LIB_DIR="$LUWRAIN_HOME/lib"
LUWRAIN_JNI_DIR="$LUWRAIN_HOME/jni"

for i in extensions registry; do
mkdir -p ~/.luwrain/$i
done

CLASS_PATH=":$(jars "$LUWRAIN_LIB_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_JAR_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_EXT_DIR/.")"

exec java \
-cp "$CLASS_PATH" \
-Djava.library.path="$LUWRAIN_JNI_DIR" \
"$LUWRAIN_MAIN_CLASS" \
--registry-dir="$LUWRAIN_REGISTRY_DIR" \
--lang="$LUWRAIN_LANG" \
--os=org.luwrain.linux.Linux \
--data-dir="$LUWRAIN_DATA_DIR" \
--user-home-dir="$LUWRAIN_USER_HOME_DIR" \
"$@" &> ~/.luwrain/log
