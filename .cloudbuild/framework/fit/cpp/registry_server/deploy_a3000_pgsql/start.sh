#!/bin/bash

set -eu

cat /etc/os-release

env

APP_DIR=$(cd "$(dirname "$0")" || exit; pwd)
echo "$APP_DIR"
cd "$APP_DIR" || exit

export LD_LIBRARY_PATH="${APP_DIR}/lib:${APP_DIR}/third_party:${LD_LIBRARY_PATH:-}"
env
echo "$LD_LIBRARY_PATH"
REGISTRY_EXT_ARGS=${REGISTRY_EXT_ARGS:-""}
# for test
# export FIT_ENV=alpha
# export HOSTIP='127.0.0.1'
# export REGISTRY_HTTPS_PORT=8003
# export FIT_HTTP_PATH='/fit'
# APP_DIR='/opt/fit/registry_server'
# APP='registry_server'
# REGISTRY_PGPASSWORD=111111

start_args=""

# run
REGISTRY_HTTP_PORT=${REGISTRY_HTTP_PORT:-0}
REGISTRY_HTTPS_PORT=${REGISTRY_HTTPS_PORT:-0} # 从环境变量中读取 ADD

REGISTRY_CERT_PATH=${REGISTRY_CERT_PATH:-"/cert"} # 从环境变量中读取 ADD
REGISTRY_SCC_PATH=${REGISTRY_SCC_PATH:-"/scc"} # 从环境变量中读取 ADD

# copy cert
# personal -> scc -> conf -> scc.conf
# personal -> scc -> ks -> primary.ks
# personal -> scc -> ks -> standby.ks

REGISTRY_SCC_CONF_PATH="/scc.conf"
REGISTRY_PERSONAL_PATH="/personal/"
rm -rf ${REGISTRY_PERSONAL_PATH}
mkdir ${REGISTRY_PERSONAL_PATH}
cp -rf ${REGISTRY_CERT_PATH} ${REGISTRY_PERSONAL_PATH}
cp -rf ${REGISTRY_SCC_PATH} ${REGISTRY_PERSONAL_PATH}
REGISTRY_CERT_PATH="${REGISTRY_PERSONAL_PATH}/${REGISTRY_CERT_PATH}"
REGISTRY_SCC_PATH="${REGISTRY_PERSONAL_PATH}/${REGISTRY_SCC_PATH}"
rm -rf ${REGISTRY_SCC_PATH}/conf
mkdir ${REGISTRY_SCC_PATH}/conf
cp -rf $REGISTRY_SCC_CONF_PATH ${REGISTRY_SCC_PATH}/conf
REGISTRY_SCC_CONF_PATH="${REGISTRY_SCC_PATH}/conf/scc.conf"
# Authorize config
chown -R registry:edatamate ${REGISTRY_PERSONAL_PATH}
chmod -R 600 ${REGISTRY_CERT_PATH}/internal/*

REGISTRY_SCC_CONF_PATH=${REGISTRY_SCC_PATH}/conf/scc.conf
if [ ${REGISTRY_HTTPS_PORT} != 0 ]; then
    REGISTRY_CERT_NAME="fit-registry-${HOSTIP}"
    REGISTRY_HTTPS_CER=${REGISTRY_CERT_PATH}/internal/${REGISTRY_CERT_NAME}.crt
    REGISTRY_HTTPS_PRIVATE_KEY=${REGISTRY_CERT_PATH}/internal/${REGISTRY_CERT_NAME}.key
    REGISTRY_HTTPS_PRIVATE_KEY_PWD=${REGISTRY_HTTPS_PRIVATE_KEY_PWD:-""}
    REGISTRY_HTTPS_PRIVATE_KEY_PWD_FILE=${REGISTRY_CERT_PATH}/internal/encryptedPwd.txt
    REGISTRY_HTTPS_CA_CRT=${REGISTRY_CERT_PATH}/internal/ca.crt

    start_args="${start_args} --https.host=${HOSTIP} --https.port=${REGISTRY_HTTPS_PORT} --https.path=${FIT_HTTP_PATH} \
    --https.cer=${REGISTRY_HTTPS_CER} --https.private-key=${REGISTRY_HTTPS_PRIVATE_KEY} \
    --https.private-key-pwd=${REGISTRY_HTTPS_PRIVATE_KEY_PWD} \
    --https.private-key-pwd-file=${REGISTRY_HTTPS_PRIVATE_KEY_PWD_FILE} \
    --https.ca-crt=${REGISTRY_HTTPS_CA_CRT} \
    --scc-crypto.config-file=${REGISTRY_SCC_CONF_PATH} \
    --pg.password=\"${REGISTRY_PGPASSWORD}\" \
    --https.ssl-verify=true"
fi
if [ ${REGISTRY_HTTP_PORT} != 0 ]; then
    start_args="${start_args} --http.host=${HOSTIP} --http.port=${REGISTRY_HTTP_PORT} --http.path=${FIT_HTTP_PATH} \
    --scc-crypto.config-file=${REGISTRY_SCC_CONF_PATH}"
fi
FIT_WORKER_ID="fit-registry-${HOSTNAME}"

# 引擎基础运行配置
sed -i "s/{templete_environment}/${FIT_ENV}/g" "${APP_DIR}/conf/fit_registry_http.json"
sed -i "s/{templete_registry_worker_id}/${FIT_WORKER_ID}/g" "${APP_DIR}/conf/fit_registry_http.json"
SED_APP_DIR=$(echo "${APP_DIR}" | sed 's/\//\\\//g')
sed -i "s/{template_app_dir}/${SED_APP_DIR}/g" "${APP_DIR}/conf/fit_registry_http.json"

if [ ${REGISTRY_HTTPS_PORT} != 0 ]; then
    bash "${APP_DIR}"/generate_crt.sh ${REGISTRY_CERT_NAME} ${REGISTRY_CERT_PATH} ${REGISTRY_SCC_PATH}
fi

chown -R registry:edatamate /applog

cd bin
exec chroot --userspec=+1400 --skip-chdir / \
    "${APP_DIR}"/bin/FitWorker --config_file="${APP_DIR}/conf/fit_registry_http.json" --app.name=${APP} \
    --worker_id="${FIT_WORKER_ID}" --environment=${FIT_ENV} --environment_chain=${FIT_ENV} \
    ${start_args} ${REGISTRY_EXT_ARGS}
