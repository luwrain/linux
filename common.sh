#!/bin/sh -e

ENV_LUWRAIN_HOME="$LUWRAIN_HOME"

if [ -r ~/.luwrain.conf ]; then
    . ~/.luwrain.conf
fi

THIS="${0##*/}"

if [ -n "$ENV_LUWRAIN_HOME" ]; then
    LUWRAIN_HOME="$ENV_LUWRAIN_HOME"
fi

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

if [ -z "$LUWRAIN_SPEECH" ]; then
    LUWRAIN_SPEECH=org.luwrain.linux.speech.command
fi

if [ -z "$LUWRAIN_SPEECH_COMMAND" ]; then
    LUWRAIN_SPEECH_COMMAND='espeak | aplay'
fi

if [ -z "$LUWRAIN_INTERACTION" ]; then
    LUWRAIN_INTERACTION=org.luwrain.interaction.javafx.JavaFxInteraction
fi

LUWRAIN_MAIN_CLASS=org.luwrain.core.Init
LUWRAIN_USER_HOME_DIR=~
LUWRAIN_APPS_DIR=~/.luwrain/app
LUWRAIN_REGISTRY_DIR=~/.luwrain/registry
LUWRAIN_DATA_DIR="$LUWRAIN_HOME/data"
LUWRAIN_JAR_DIR="$LUWRAIN_HOME/jar"
LUWRAIN_LIB_DIR="$LUWRAIN_HOME/lib"
LUWRAIN_JNI_DIR="$LUWRAIN_HOME/jni"

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

CLASS_PATH=":$(jars "$LUWRAIN_LIB_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_JAR_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(jars "$LUWRAIN_APPS_DIR/.")"

exec java \
-cp "$CLASS_PATH" \
-Djava.library.path="$LUWRAIN_JNI_DIR" \
"$LUWRAIN_MAIN_CLASS" \
--registry-dir="$LUWRAIN_REGISTRY_DIR" \
--lang="$LUWRAIN_LANG" \
--os=org.luwrain.linux.Linux \
--speech="$LUWRAIN_SPEECH" \
--speech-command="$LUWRAIN_SPEECH_COMMAND" \
--data-dir="$LUWRAIN_DATA_DIR" \
--user-home-dir="$LUWRAIN_USER_HOME_DIR" \
--interaction="$LUWRAIN_INTERACTION" \
"$@" &> ~/.luwrain.log
