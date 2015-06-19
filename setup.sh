#!/bin/sh
# Performs initial Luwrain setup in users home (creates ~/.luwrain and ~/.luwrain.conf)

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

cat <<EOF > ~/.luwrain/registry/org/luwrain/pim/news/storing/strings.txt
"type" = "jdbc"
"driver" = "org.hsqldb.jdbc.JDBCDriver"
"url" = "jdbc:hsqldb:file:$HSQLDB/news"
"login" = "sa"
"passwd" = ""

EOF
