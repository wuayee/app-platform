#！/bin/bash
set -ex

#获取服务参数
image_name=$1
image_tag=$2
framework_version=$3

#获取服务路径
CRTDIR=$(pwd)
settings_file=${CRTDIR}/.ci/settings.xml

#下载框架包并解压缩
mkdir fit
if [ -z "$(ls -A fit)" ]; then
  rm -rf fit/*
fi
mkdir -p fit/custom
mkdir -p fit/fitframework
cd fit/fitframework || exit
#if [[ ${framework_version} =~ [sS][nN][aA][pP][sS][hH][oO][tT]$ ]]; then
#  type="snapshot"
#else
#  type="release"
#fi
wget --no-check-certificate https://cmc-nkg-artifactory.cmc.tools.huawei.com/artifactory/sz-software-release/fitlab/release/fit-fenghua-hakuna-java-service/"${framework_version}"/fit_"${framework_version}".tar.gz
tar -xvzf fit_"${framework_version}".tar.gz
rm fit_"${framework_version}".tar.gz

rm -f ./plugins/fit-service-coordination-simple-"${framework_version}".jar
rm -f ./lib/fit-log-consol*

# 将 fit-log-log4j2 插件放置在 fit-fitframework/lib/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fitframework \
  -DartifactId=fit-log-log4j2 \
  -Dversion=${framework_version} \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fitframework:fit-log-log4j2:${framework_version}:jar -DoutputDirectory=./lib/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 log4j-api 插件放置在 fit-fitframework/shared/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=org.apache.logging.log4j \
  -DartifactId=log4j-api \
  -Dversion=2.18.0 \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=org.apache.logging.log4j:log4j-api:2.18.0:jar -DoutputDirectory=./shared/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 log4j-core 插件放置在 fit-fitframework/shared/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=org.apache.logging.log4j \
  -DartifactId=log4j-core \
  -Dversion=2.18.0 \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=org.apache.logging.log4j:log4j-core:2.18.0:jar -DoutputDirectory=./shared/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 log4j-slf4j-impl 插件放置在 fit-fitframework/shared/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=org.apache.logging.log4j \
  -DartifactId=log4j-slf4j-impl \
  -Dversion=2.20.0 \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=org.apache.logging.log4j:log4j-slf4j-impl:2.20.0:jar -DoutputDirectory=./shared/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 slf4j-api 插件放置在 fit-fitframework/shared/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=org.slf4j \
  -DartifactId=slf4j-api \
  -Dversion=1.7.36 \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=org.slf4j:slf4j-api:1.7.36:jar -DoutputDirectory=./shared/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#下载it-security-kms和fit-sso-filter-his包
cd ${CRTDIR}/fit/custom
wget --no-check-certificate https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/com/huawei/fit/plugin/fit-security-kms/"${framework_version}"/fit-security-kms-"${framework_version}".jar
wget --no-check-certificate https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/com/huawei/fit/plugin/fit-sso-filter-his/"${framework_version}"/fit-sso-filter-his-"${framework_version}".jar

cd "${CRTDIR}" || exit
#Step1 编译jober-genericable
mvn -f jane-common clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f jober-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f waterflow-edatamate-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f flow-graph clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f jober-multiversion-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step2 编译quality-planning-plugin、cloud-core-plugin、clouddesign-req-plugin、louddrago-plugin、car-bu-plugin插件模块
mvn -f plugins/account-change-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/account-change-plugin/target/*.jar ./fit/custom/
mvn -f plugins/quality-planning-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/quality-planning-plugin/target/*.jar ./fit/custom/
mvn -f plugins/cloud-core-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/cloud-core-plugin/target/*.jar ./fit/custom/
mvn -f plugins/clouddesign-req-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/clouddesign-req-plugin/target/*.jar ./fit/custom/
mvn -f plugins/net-tools-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/net-tools-plugin/target/*.jar ./fit/custom/
mvn -f plugins/clouddragon-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/clouddragon-plugin/target/*.jar ./fit/custom/
mvn -f plugins/specialmanagement-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/specialmanagement-plugin/target/*.jar ./fit/custom/
mvn -f plugins/libing-idp-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/libing-idp-plugin/target/*.jar ./fit/custom/
mvn -f plugins/libing-visionit-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/libing-visionit-plugin/target/*.jar ./fit/custom/
mvn -f plugins/gdd-pi-assessment-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/gdd-pi-assessment-plugin/target/*.jar ./fit/custom/
mvn -f plugins/opensource-dzion-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/opensource-dzion-plugin/target/*.jar ./fit/custom/
mvn -f plugins/wxalm-trusttest-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/wxalm-trusttest-plugin/target/*.jar ./fit/custom/

# 将 model 插件放置在 fit/custom/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fit.jade.eco \
  -DartifactId=model \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fit.jade.eco:model:1.0.0-SNAPSHOT:jar -DoutputDirectory=./fit/custom/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 rag 插件放置在 fit/custom/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fit.jade.eco \
  -DartifactId=rag \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fit.jade.eco:rag:1.0.0-SNAPSHOT:jar -DoutputDirectory=./fit/custom/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

# 将 tool 相关插件放置在 fit/custom/ 目录下
mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fit.jade \
  -DartifactId=store-item-orm-mybatis \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fit.jade:store-item-orm-mybatis:1.0.0-SNAPSHOT:jar -DoutputDirectory=./fit/custom/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fit.jade \
  -DartifactId=store-tool-execution \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fit.jade:store-tool-execution:1.0.0-SNAPSHOT:jar -DoutputDirectory=./fit/custom/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.2:get \
  -DgroupId=com.huawei.fit.jade \
  -DartifactId=tool-waterflow \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackaging=jar \
  -DremoteRepositories=https://cmc.centralrepo.rnd.huawei.com/artifactory/product_maven/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn dependency:copy -Dartifact=com.huawei.fit.jade:tool-waterflow:1.0.0-SNAPSHOT:jar -DoutputDirectory=./fit/custom/ \
  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step3 编译flowable-flow-plugin、tianzhou-plugin
mvn -f plugins/flowable-flow-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/flowable-flow-plugin/target/*.jar ./fit/custom/
mvn -f task-default-driver clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/task-default-driver/target/*.jar ./fit/custom/
mvn -f plugins/tianzhou-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/tianzhou-plugin/target/*.jar ./fit/custom/
mvn -f plugins/task-codehub-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/task-codehub-plugin/target/*.jar ./fit/custom/
mvn -f plugins/s3-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/s3-plugin/target/*.jar ./fit/custom/
mvn -f flowable-ohscript-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/flowable-ohscript-plugin/target/*.jar ./fit/custom/
mvn -f plugins/w3-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/w3-plugin/target/*.jar ./fit/custom/
mvn -f dynamic-form-genericable clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f jane-common-component clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f plugins/form-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/form-plugin/target/*.jar ./fit/custom/
mvn -f plugins/aipp-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/plugins/aipp-plugin/target/*.jar ./fit/custom/

mvn -f flow-graph-db-driver clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/flow-graph-db-driver/target/*.jar ./fit/custom/

#Step4 编译jober
mvn -f jober clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/jober/target/*.jar ./fit/custom/

mvn -f health-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
cp ${CRTDIR}/health-plugin/target/*.jar ./fit/custom/

#Step5 打包镜像
sudo docker build -t fit/${image_name}:${image_tag} --pull=true --file=${CRTDIR}/Dockerfile ${CRTDIR}/
