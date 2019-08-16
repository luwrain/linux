#!/bin/bash -e
# Main script to launch LUWRAIN with external JRE
# Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>
# This file is part of LUWRAIN.

THIS="${0##*/}"

if [ -z "$LUWRAIN_HOME" ]; then
    if [ -e ~/luwrain/jar/luwrain.jar ]; then
	LUWRAIN_HOME=~/luwrain
    elif [ -e /opt/luwrain/jar/luwrain.jar ]; then
	LUWRAIN_HOME=/opt/luwrain
    else
	echo "$THIS: unable to find the directory with LUWRAIN installation (must be either ~/luwrain or /opt/luwrain)" >&2
	exit 1
    fi
fi

cd "$LUWRAIN_HOME"

exec java \
     -Djava.library.path="$LUWRAIN_HOME" \
     -jar jar/luwrain-base.jar "$@"
