#!/bin/bash
# Creates ~/.luwrain and performs initial LUWRAIN setup
# Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

cd jni
make &> /tmp/.luwrain.make.log
cd ..

mkdir -p ~/.luwrain
for i in extensions registry; do
    mkdir -p ~/.luwrain/$i
done 

cp -r hsqldb ~/.luwrain
cp -r registry/. ~/.luwrain/registry
cp -r i18n/ru/. ~/.luwrain/registry/

HSQLDB=~/.luwrain/hsqldb

for i in news mail contacts; do
cat <<EOF > ~/.luwrain/registry/org/luwrain/pim/$i/storing/strings.txt
"type" = "jdbc"
"driver" = "org.hsqldb.jdbc.JDBCDriver"
"url" = "jdbc:hsqldb:file:$HSQLDB/$i"
"login" = "sa"
"passwd" = ""
EOF
done

echo 'Installation completed successfully!'
