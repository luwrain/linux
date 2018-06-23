#!/bin/bash -e
# The main script to launch LUWRAIN with external JRE
# Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>
# This file is part of LUWRAIN.

THIS="${0##*/}"

if [ -z "$LUWRAIN_HOME" ]; then
    if [ -e ~/luwrain/jar/luwrain.jar ]; then
	LUWRAIN_HOME=~/luwrain
    elif [ -e /opt/luwrain/jar/luwrain.jar ]; then
	LUWRAIN_HOME=/opt/luwrain
    else
	echo "$THIS:unable to find the directory with LUWRAIN distribution (must be ~/luwrain or /opt/luwrain)" >&2
	exit 1
    fi
fi

cd "$LUWRAIN_HOME"

exec java \
-cp jar/luwrain.jar:jar/luwrain-base.jar \
-Djava.security.egd=file:/dev/./urandom \
-Dprism.order=sw \
-Djava.library.path="$LUWRAIN_HOME" \
org.luwrain.core.Init "$@"
