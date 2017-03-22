#!/bin/bash -e
# Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>
# This file is part of LUWRAIN.

THIS="${0##*/}"
LUWRAIN_MAIN_CLASS=org.luwrain.core.Init

if [ -z "$LUWRAIN_HOME" ]; then
    if [ -e ~/luwrain/jar/luwrain ]; then
	LUWRAIN_HOME=~/luwrain
    elif [ -e /opt/luwrain/jar/luwrain.jar ]; then
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

cd "$LUWRAIN_HOME"

exec java \
-cp jar/luwrain.jar:jar/luwrain-base.jar \
-Djava.security.egd=file:/dev/./urandom \
-Djava.library.path="$LUWRAIN_HOME" \
"$LUWRAIN_MAIN_CLASS" \
