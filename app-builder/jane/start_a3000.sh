#!/bin/sh
set -e

execute_time=`date`
echo "/***********************************************************************"
echo " *"
echo " *  Start Jane Jober Service"
echo " *  Execute Date : $execute_time"
echo " *"
echo " ***********************************************************************/"

echo ""
echo "Launch Application..."

export REST_PORT=8004
export DSF_PORT=7003

# Java 初始化容器证书和SCC 密钥开始 ****
# 复制SCC 秘钥
source /cert-scc-scripts/prepare_scc.sh jane jane
# 复制公共证书文件至容器自用证书目录, 解析证书配置文件生成相应环境变量
source /cert-scc-scripts/prepare_personal_cert.sh jane jane
# 拼接身份证书
source /cert-scc-scripts/combine_cert_chain.sh jane jane
# 生成 keystore文件
source /cert-scc-scripts/generate_java_jks.sh jane jane

echo "try to exportServerSslEnv"
encryptedPwd=$GLOBAL_PWD
export SERVER_HTTP_SECURE_TRUST_STORE_FILE=$GLOBAL_TRUST_JKS
export SERVER_HTTP_SECURE_KEY_STORE_FILE=$GLOBAL_WITH_CHAIN_P12
export SERVER_HTTP_SECURE_TRUST_STORE_PASSWORD="${encryptedPwd}"
export SERVER_HTTP_SECURE_KEY_STORE_PASSWORD="${encryptedPwd}"
export CLIENT_HTTP_SECURE_TRUST_STORE_FILE=$GLOBAL_TRUST_JKS
export CLIENT_HTTP_SECURE_KEY_STORE_FILE=$GLOBAL_WITH_CHAIN_P12
export CLIENT_HTTP_SECURE_TRUST_STORE_PASSWORD="${encryptedPwd}"
export CLIENT_HTTP_SECURE_KEY_STORE_PASSWORD="${encryptedPwd}"

export PLUGIN_SCC_CONF_FILE_PATH=$SCC_CONF_PATH

echo "ssl preparation completed, try to start the application"
# Java 初始化容器证书和SCC 密钥结束 ****

# root下更改pvc存储 属主为jane，组为jane   （以具体挂载的pvc为准）
chown -R jane:jane /tmp
chown -R jane:jane ${LOG_HOME}
chown -R jane:jane /usr/local/java
chown jane:jane $APP_PATH/$APP
chown jane:jane /opt/jane/start.sh
# root下exec   chroot ，使用jane用户启动业务进程，不要使用root运行进程
exec chroot --userspec=+1888:1888 / java $JAVA_OPTS -Xms2048m -Xmx5100m \
-Dserver.http.secure.port=8004 \
-Dserver.http.port=8888 \
-Dserver.http.secure.trust-store-file=${SERVER_HTTP_SECURE_TRUST_STORE_FILE} \
-Dserver.http.secure.key-store-file=${SERVER_HTTP_SECURE_KEY_STORE_FILE} \
-Dserver.http.secure.trust-store-password=${SERVER_HTTP_SECURE_TRUST_STORE_PASSWORD} \
-Dserver.http.secure.key-store-password=${SERVER_HTTP_SECURE_KEY_STORE_PASSWORD} \
-Dclient.http.secure.trust-store-file=${CLIENT_HTTP_SECURE_TRUST_STORE_FILE} \
-Dclient.http.secure.key-store-file=${CLIENT_HTTP_SECURE_KEY_STORE_FILE} \
-Dclient.http.secure.trust-store-password=${CLIENT_HTTP_SECURE_TRUST_STORE_PASSWORD} \
-Dclient.http.secure.key-store-password=${CLIENT_HTTP_SECURE_KEY_STORE_PASSWORD} \
-Dplugin.scc.conf-file-path=${PLUGIN_SCC_CONF_FILE_PATH} \
-Dmatata.registry.protocol=4 \
-Dclient.http.secure.ignore-hostname=true \
-Dclient.http.secure.encrypted=true \
-Dserver.http.secure.encrypted=true \
-jar $APP_PATH/$APP

echo "Application started..."