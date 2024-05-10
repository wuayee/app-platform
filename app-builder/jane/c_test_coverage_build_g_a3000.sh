#！/bin/bash
set -ex

#获取服务参数

#获取服务路径
CRTDIR=$(pwd)
settings_file=${CRTDIR}/.ci/settings.xml

#Step1 编译jober-genericable
mvn -f jober-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step2 执行测试覆盖率
# 本地执行命令： mvn clean -f jober dt4j-coverage:aggregate-report-diff -DactiveCoverage -DskipTests=false  -Dmaven.test.failure.ignore=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Djacoco-agent.destfile=target/jacoco.exec -DscmVersion=develop -Duser.name=g00564732 -U -gs .ci/settings.xml
mvn clean -f jober com.huawei.dt:dt4j-coverage-maven-plugin:aggregate-report-diff -U -s ${settings_file} -gs ${settings_file} \
-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal \
-DactiveCoverage -DskipTests=false -Dusername=${gitUser} -Dpassword=${gitPwd} -Ddt.brand=HDT-JAVA \
-DscmVersionType=branch -DscmVersion=${TARGET_BRANCH} -DconnectionUrl=scm:git:${TARGET_REPO} -Dmaven.test.failure.ignore=true -Djacoco-agent.destfile=target/jacoco.exec