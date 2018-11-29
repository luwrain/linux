#!/bin/sh

GREETER_DIR=/opt/greeter
GREETER_SHELL_COMMAND=/usr/bin/yasr
GREETER_SPEECH_COMMAND=
RECORDING_COMMAND="/usr/bin/ogg123 -d alsa $GREETER_DIR/greeting.ogg"
FRAMES_DIR="$GREETER_DIR/frames"

/usr/bin/greeter \
"$RECORDING_COMMAND" \
"$GREETER_SPEECH_COMMAND" \
"$GREETER_SHELL_COMMAND" \
$(find "$FRAMES_DIR" -type f | sort)
