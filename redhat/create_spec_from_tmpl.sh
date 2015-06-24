#!/bin/sh
DATE=`date "+%Y%m%d%H%M"`
COMMIT=`git rev-parse HEAD`
SHORTCOMMIT=`git rev-parse --short=8 HEAD`

cp nodeagent2.spec.tmpl nodeagent2.spec

sed -i -e "s;@@DATE@@;${DATE};" nodeagent2.spec
sed -i -e "s;@@COMMIT@@;${COMMIT};" nodeagent2.spec
sed -i -e "s;@@SHORTCOMMIT@@;${SHORTCOMMIT};" nodeagent2.spec
