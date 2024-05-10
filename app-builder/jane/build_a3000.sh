#！/bin/bash
set -ex

#获取服务参数
app=$1
image_name=$2
image_tag=$3
module_name=$4

#获取服务路径
CRTDIR=$(pwd)
settings_file=${CRTDIR}/.ci/settings.xml

#Step1 编译jober-genericable
mvn -f jane-common clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f jober-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step2 编译quality-planning-plugin、cloud-core-plugin、clouddesign-req-plugin、louddrago-plugin、car-bu-plugin插件模块
mvn -f plugins/account-change-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/quality-planning-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/cloud-core-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/clouddesign-req-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/net-tools-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/clouddragon-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/specialmanagement-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/libing-idp-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/libing-visionit-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#Step3 编译flowable-flow-plugin、tianzhou-plugin
mvn -f plugins/flowable-flow-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f task-default-driver clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task-default-driver-a3000 clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/tianzhou-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/task-codehub-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/s3-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flowable-ohscript-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/w3-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f dynamic-form-genericable clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f jane-common-component clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/form-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f plugins/aipp-plugin clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph-elsa-driver clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step4 编译jober
mvn -f jober clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step5 编译fit-start
mvn -f ${module_name} clean install -P '!registry' -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step6 定义包名路径，并将包移动到dockerfile同级目录
module_path=${CRTDIR}/${module_name}
app_path=${module_path}/target/${app}
ls -l ${app_path}
mv ${app_path} ${CRTDIR}/

#Step7 打包镜像
sudo docker build --build-arg APP=${app} -t fit/${image_name}:${image_tag} --pull=true --file=${CRTDIR}/Dockerfile ${CRTDIR}/
