#!/bin/bash

set -e
HOSTIP=${HOSTIP:-"localhost"} # 从环境变量中读取
REGISTRY_CERT_NAME=${1:-"fit-registry-${HOSTIP}"}
REGISTRY_CERT_PATH=${2:-"/personal/cert"}
REGISTRY_SCC_PATH=${3:-"/personal/scc"}
REGISTRY_SEC_COMPONENT_PATH=${REGISTRY_SEC_COMPONENT_PATH:-"/usr/local/seccomponent/"} # 从环境变量中读取 option
REGISTRY_SCC_CONF_PATH=${REGISTRY_SCC_PATH}/conf/scc.conf

lock_file="${REGISTRY_CERT_PATH}/internal/ca.crt"
#  监听文件锁，作为ca.crt是否存在的依据
function wait_ca_crt() {
    # 尝试查询ca.crt
    if [[ ! -e "$lock_file" ]]; then
        echo "cert is being generated, waiting..."
        # 一直等待ca.crt创建成功
        while [[ ! -e "$lock_file" ]]; do
            sleep 1
            echo "cert is being generated, waiting..."
        done
    fi
}

function genercate_crt() {
    cd ${REGISTRY_CERT_PATH}/internal
    encrypted_password_file_path="${REGISTRY_CERT_PATH}/internal/encryptedPwd.txt"
    scc_config_path="${REGISTRY_SCC_CONF_PATH}"

    encrypted_password=$(cat ${encrypted_password_file_path} | head -n 1)
    decrypted_pwd=$(echo ${encrypted_password} | ${REGISTRY_SEC_COMPONENT_PATH}/bin/CryptoAPI -f ${scc_config_path} \
        -d | awk -F':' '{print $2}')
    if [ -z "$decrypted_pwd" ]; then
        echo "password is empty."
        cd -
        exit 1
    fi
    echo "/C=CN/ST=Beijing/L=Beijing/O=HUAWEI/CN=${HOSTIP}"

    openssl genrsa -aes256 -passout pass:${decrypted_pwd} -out ${REGISTRY_CERT_NAME}.key 3072
    openssl req -new -key ${REGISTRY_CERT_NAME}.key -passin pass:${decrypted_pwd} -out ${REGISTRY_CERT_NAME}.csr -subj \
        "/C=CN/ST=Beijing/L=Beijing/O=HUAWEI/CN=${HOSTIP}"
    openssl x509 -req -in ${REGISTRY_CERT_NAME}.csr -CAkey ${REGISTRY_CERT_PATH}/internal/ca.key \
        -passin pass:${decrypted_pwd} -out ${REGISTRY_CERT_NAME}.crt -CA ${REGISTRY_CERT_PATH}/internal/ca.crt \
        -CAcreateserial -days 36500
    chmod 600 ${REGISTRY_CERT_PATH}/internal/${REGISTRY_CERT_NAME}.key
    chmod 600 ${REGISTRY_CERT_PATH}/internal/${REGISTRY_CERT_NAME}.csr
    chmod 600 ${REGISTRY_CERT_PATH}/internal/${REGISTRY_CERT_NAME}.crt
    chown -R registry:edatamate ${REGISTRY_CERT_PATH}/internal
    cd -
}

wait_ca_crt
genercate_crt