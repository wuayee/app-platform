echo "build fit-java"
echo "buildVersion=${FRAMEWORK_VERSION}">${WORKSPACE}/buildInfo.properties

# 支持https
prePath=$(pwd)
cd "${JAVA_HOME}/jre/lib/security"
wget --no-check-certificate https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/CMC-Release/certificates/HuaweiITRootCA.cer
wget --no-check-certificate https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/CMC-Release/certificates/HWITEnterpriseCA1.cer
keytool -keystore cacerts -importcert -alias HuaweiITRootCA -file HuaweiITRootCA.cer -storepass changeit -noprompt
keytool -keystore cacerts -importcert -alias HWITEnterpriseCA1 -file HWITEnterpriseCA1.cer -storepass changeit -noprompt
chmod 755 -R $JAVA_HOME/jre/lib/security/cacerts

cd "${JAVA_HOME}/jre/lib/security"
keytool -list -V -keystore cacerts -storepass changeit | grep -i HuaweiITRootCA
keytool -list -V -keystore cacerts -storepass changeit | grep -i HWITEnterpriseCA1

rm -f ./HuaweiITRootCA.cer
rm -f ./HWITEnterpriseCA1.cer

cd "${prePath}/framework/fit/java"

echo "start to execute maven"
mvn -U clean deploy -Pfit -gs "${prePath}/.cloudbuild/framework/fit/java/settings.xml"

# 框架打包并上传
cd "${prePath}"
echo "workSpace: " "${WORKSPACE}"
packageDir="${WORKSPACE}/package/"
mkdir -p "${packageDir}"
if [ -z "$(ls -A "${packageDir}")" ]; then
  rm -rf "${packageDir:?}"/*
fi

mv "${prePath}/framework/fit/java/target"/* "${packageDir}"
cd "${packageDir}" || exit
echo "frameworkName: " "${FRAMEWORK_NAME}"
echo "frameworkVersion: " "${FRAMEWORK_VERSION}"
packageName=${FRAMEWORK_NAME}_${FRAMEWORK_VERSION}.tar.gz
tar -zcvPf "${packageName}" *