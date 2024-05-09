#!/bin/bash
echo "buildVersion=${FRAMEWORK_VERSION}">${WORKSPACE}/buildInfo.properties
node -v
npm -v

cd ./framework/elsa/fit-elsa

npm install
npm run build

echo 'start publish-------------------------'
pwd
ls -al
npm config set strict-ssl false

npm publish