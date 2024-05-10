echo "build jane-task"

# 支持https
prePath=$(pwd)
cd $JAVA_HOME/jre/lib/security
wget --no-check-certificate https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/CMC-Release/certificates/HuaweiITRootCA.cer
wget --no-check-certificate https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/CMC-Release/certificates/HWITEnterpriseCA1.cer
keytool -keystore cacerts -importcert -alias HuaweiITRootCA -file HuaweiITRootCA.cer -storepass changeit -noprompt
keytool -keystore cacerts -importcert -alias HWITEnterpriseCA1 -file HWITEnterpriseCA1.cer -storepass changeit -noprompt
chmod 755 -R $JAVA_HOME/jre/lib/security/cacerts

cd $JAVA_HOME/jre/lib/security
keytool -list -V -keystore cacerts -storepass changeit | grep -i HuaweiITRootCA
keytool -list -V -keystore cacerts -storepass changeit | grep -i HWITEnterpriseCA1

rm -f ./HuaweiITRootCA.cer
rm -f ./HWITEnterpriseCA1.cer

cd $prePath/task

echo "start to execute maven"
mvn -U clean deploy -gs "../.cloudbuild/task/settings.xml"