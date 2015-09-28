#!/bin/sh
# Creates ~/.luwrain and performs initial LUWRAIN setup there

cd jni
make
cd ..

mkdir -p ~/.luwrain
for i in app extensions registry; do
    mkdir -p ~/.luwrain/$i
done 

cp -r hsqldb ~/.luwrain
cp -r registry/. ~/.luwrain/registry
cp -r news/ru/. ~/.luwrain/registry/org/luwrain/pim/news/groups/

cat luwrain.conf.def > ~/.luwrain.conf

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
