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
certPath=/cert/internal
sccPath=/scc
personalSccPath=/sccPersonal
personalCertPath=/certPersonal
caCert="${personalCertPath}/ca.crt"
encryptedFile="${personalCertPath}/encryptedPwd.txt"
commonKeystore="${certPath}/global.p12"
# Java专用的keystore 文件，需要从nginx 生成的文件复制过来
backendKeystore="${personalCertPath}/global.p12"
truststore="${personalCertPath}/caTruststore.jks"
sccConf="/scc.conf"

while [ ! -e $commonKeystore ]; do
    sleep 1
    echo "cert haven't been generated yet"
done

cp -r $sccPath $personalSccPath
cp -r $certPath $personalCertPath

echo "try to import cert to keystore"
pwd=$(/usr/local/seccomponent/bin/CryptoAPI -f ${sccConf} -d < ${encryptedFile} | awk -F ':' '{print $2}')
keytool -keystore ${truststore} -keypass "${pwd}" -storepass "${pwd}" -alias ca -import -trustcacerts -file ${caCert} -noprompt
unset pwd

echo "try to exportServerSslEnv"
encryptedPwd=$(cat ${encryptedFile})
export SERVER_HTTP_SECURE_TRUST_STORE_FILE=${truststore}
export SERVER_HTTP_SECURE_KEY_STORE_FILE=${backendKeystore}
export SERVER_HTTP_SECURE_TRUST_STORE_PASSWORD="${encryptedPwd}"
export SERVER_HTTP_SECURE_KEY_STORE_PASSWORD="${encryptedPwd}"
export CLIENT_HTTP_SECURE_TRUST_STORE_FILE=${truststore}
export CLIENT_HTTP_SECURE_KEY_STORE_FILE=${backendKeystore}
export CLIENT_HTTP_SECURE_TRUST_STORE_PASSWORD="${encryptedPwd}"
export CLIENT_HTTP_SECURE_KEY_STORE_PASSWORD="${encryptedPwd}"

export PLUGIN_SCC_CONF_FILE_PATH=${sccConf}

chown -R jane:edatamate $personalSccPath
chown -R jane:edatamate $sccConf
chmod -R 600 $sccConf
chown -R jane:edatamate $personalCertPath
chmod -R 600 $personalCertPath
chmod 700 $personalCertPath

echo "ssl preparation completed, try to start the application"
# Java 初始化容器证书和SCC 密钥结束 ****

# root下更改pvc存储 属主为jane，组为jane   （以具体挂载的pvc为准）
chown -R jane:jane /tmp
chown -R jane:jane /applog
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