#！/bin/bash
#set -x
set -e
node -v
npm -v

export NODE_TLS_REJECT_UNAUTHORIZED=0

#npm install
npm config set strict-ssl false
# 强制清除缓存
npm cache clean -f
npm install --force

# 打包静态资源
npm run build

# 打包产物
ls -l
#打包build中所有文件，并且不包含build目录结构
zip -rj modelEngine.${ENV_PIPELINE_STARTTIME}.zip build/*
