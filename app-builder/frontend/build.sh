#！/bin/bash
#set -x
set -e
tag=$1
export SSO_URL=$2
echo "tag:"${tag}
echo "SSO_URL":${SSO_URL}
export NODE_TLS_REJECT_UNAUTHORIZED=0
cd app-builder/frontend
npm config set strict-ssl false
# 强制清除缓存
npm cache clean -f
npm install --legacy-peer-deps
npm run build:${tag}

ls -l
#打包build中所有文件，并且不包含build目录结构
zip -rj dist.${EDEVOPS_TIMESTAMP}.zip build/*
