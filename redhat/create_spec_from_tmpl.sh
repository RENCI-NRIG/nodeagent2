#!/bin/sh
DATE=`date "+%Y%m%d%H%M"`
COMMIT=`git rev-parse HEAD`
SHORTCOMMIT=`git rev-parse --short=8 HEAD`
REL_VERSION=`git describe --tags --long --dirty --always | sed s/node-agent2-// | sed s/-/_/g`
SIMPLE_VERSION=`git describe --tags | sed s/node-agent2-//`

cp nodeagent2.spec.tmpl nodeagent2.spec

sed -i -e "s;@@DATE@@;${DATE};" nodeagent2.spec
sed -i -e "s;@@COMMIT@@;${COMMIT};" nodeagent2.spec
sed -i -e "s;@@VERSION@@;${REL_VERSION};" nodeagent2.spec
sed -i -e "s;@@SIMPLE_VERSION@@;${SIMPLE_VERSION};" nodeagent2.spec
