#!/bin/sh -e

if [ -r ~/.luwrain.conf ]; then
    . ~/.luwrain.conf
fi

if [ -z "$LUWRAIN_DIR" ]; then
    LUWRAIN_DIR=~/luwrain
fi

if [ -z "$LUWRAIN_APPS_DIR" ]; then
    LUWRAIN_APPS_DIR=~/.luwrain/app
fi

if [ -z "$LUWRAIN_REGISTRY_DIR" ]; then
    LUWRAIN_REGISTRY_DIR=~/.luwrain/registry
fi

if [ -z "$LUWRAIN_LANG" ]; then
    LUWRAIN_LANG=en
fi

if [ -z "$LUWRAIN_SPEECH" ]; then
    LUWRAIN_SPEECH=org.luwrain.linux.speech.command
fi

if [ -z "$LUWRAIN_SPEECH_COMMAND" ]; then
    LUWRAIN_SPEECH_COMMAND='RHVoice -r 1.6 | aplay'
fi

LUWRAIN_DATA_DIR="$LUWRAIN_DIR/data"
LUWRAIN_JAR_DIR="$LUWRAIN_DIR/jar"
LUWRAIN_LIB_DIR="$LUWRAIN_DIR/lib"
LUWRAIN_JNI_DIR="$LUWRAIN_DIR/jni"

MAIN_CLASS=org.luwrain.core.Init
USER_HOME_DIR=~
LOG_FILE=~/.luwrain.log

collect_jars()
{
    if [ -d "$1" ]; then
	find "$1" -iname '*.jar' | xargs echo | sed s/' '/':'/g | sed s:/./:/:g
    fi
}

CLASS_PATH=":$(collect_jars "$LUWRAIN_LIB_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(collect_jars "$LUWRAIN_JAR_DIR/.")"
CLASS_PATH="$CLASS_PATH:$(collect_jars "$LUWRAIN_APPS_DIR/.")"

exec java \
-cp "$CLASS_PATH" \
-Djava.library.path="$LUWRAIN_JNI_DIR" \
"$MAIN_CLASS" \
--registry-dir="$LUWRAIN_REGISTRY_DIR" \
--lang="$LUWRAIN_LANG" \
--os=org.luwrain.linux.Linux \
--speech="$LUWRAIN_SPEECH" \
--speech-command="$LUWRAIN_SPEECH_COMMAND" \
--data-dir="$LUWRAIN_DATA_DIR" \
--user-home-dir="$USER_HOME_DIR" \
"$@" &> "$LOG_FILE"
