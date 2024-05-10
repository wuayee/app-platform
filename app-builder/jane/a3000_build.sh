#！/bin/bash
set -ex

#获取服务参数
app=$1
image_name=$2
image_tag=$3
module_name=$4
base_image=$5

#获取服务路径
CRTDIR=$(pwd)
settings_file=${CRTDIR}/.ci/settings.xml

#Step1 编译jober-genericable
mvn -f jober-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step2 编译quality-planning-plugin、cloud-core-plugin、clouddrago-plugin、car-bu-plugin插件模块
#mvn -f quality-planning-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#mvn -f cloud-core-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#mvn -f clouddragon-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#mvn -f specialmanagement-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#mvn -f libing-idp-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#mvn -f libing-visionit-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -DskipTests -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
#Step3 编译flowable-flow-plugin
mvn -f flowable-ohscript-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph-db-driver clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task-default-driver-a3000 clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step4 编译jober
mvn -f jober clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step5 编译fit-start
mvn -f ${module_name} clean install -P '!registry' -P '!ads' -P 'A3000' -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step6 定义包名路径，并将包移动到dockerfile同级目录
module_path=${CRTDIR}/${module_name}
app_path=${module_path}/target/${app}
ls -l ${app_path}
mv ${app_path} ${CRTDIR}/

#Step7 打包镜像
SERVICE_VERSION=${jane_branch}-${ENV_PIPELINE_STARTTIME}
echo "buildVersion=eDataMate-${ENV_PIPELINE_STARTTIME}" >> "${WORKSPACE}"/buildInfo.properties

mkdir sql
server_sql_list=$(find "${WORKSPACE}"/jane/jober/sql/A3000/new/ -name "*.sql")
for i in ${server_sql_list}
do
    cp "$i" sql/
done

if [ -z "${APP_VERSION}" ];then
  docker build --build-arg BASE_IMAGE=${base_image} --build-arg APP=${app} -t ${remote_harbor}/${ENV}/${image_name}:${PLATFORM}-${SERVICE_VERSION} --pull=true --file=${CRTDIR}/Dockerfile ${CRTDIR}/
  docker save -o "${image_name}.${PLATFORM}-${SERVICE_VERSION}.tar" ${remote_harbor}/${ENV}/${image_name}:${PLATFORM}-${SERVICE_VERSION}

  cd sql
  tar -cvf sql_jane_${ENV_PIPELINE_STARTTIME}.tar *
  mv sql_jane_${ENV_PIPELINE_STARTTIME}.tar ../
else
  docker build --build-arg BASE_IMAGE=${base_image} --build-arg APP=${app} -t fit/${image_name}:${image_tag} --pull=true --file=${CRTDIR}/Dockerfile ${CRTDIR}/
  docker tag fit/edatamate-jane:arm_64-${APP_VERSION} edatamate-jane:arm_64-${APP_VERSION}
  docker save -o edatamate-jane.arm_64-${APP_VERSION}.tar edatamate-jane:arm_64-${APP_VERSION}

  cd sql
  tar -cvf sql_jane_${APP_VERSION}.tar *
  mv sql_jane_${APP_VERSION}.tar ../
fi
