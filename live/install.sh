#!/bin/bash -e

THIS="${0##*/}"
PREFIX="${1%%/}"

[ -z "$PREFIX" ] && echo "$THIS:no prefix given" >&2 && exit 1

for i in etc usr/bin usr/share/luwrain-live; do
mkdir -p "$PREFIX/$i"
done

for i in frames recordings; do
cp -r $i "$PREFIX/usr/share/luwrain-live"
done

cp -r greeter/greeter scripts/. "$PREFIX/usr/bin"
cp etc/greeter.conf "$PREFIX/etc"

