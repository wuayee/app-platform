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

mvn -f ../fit/java clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f ../fit/ohscript clean install -U  -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

curl -o artget https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/CMC-Release/artget/install/prod/Latest/linux/artget
ls -l
sudo chmod 777 artget

#Step1 编译jober-genericable、task
mvn -f jober-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f waterflow-edatamate-genericable clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f health-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step2 编译flowable-ohscript-plugin、flowable-flow-plugin插件模块
mvn -f flowable-ohscript-plugin clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f flow-graph-db-driver clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal
mvn -f task-default-driver-a3000 clean install -U -s ${settings_file} -Dmaven.test.skip=true -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step3 编译jober
mvn -f jober clean install -U -s ${settings_file} -gs ${settings_file} -Dmaven.test.skip=true -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step4 编译fit-start
mvn -f ${module_name} clean install -P '!registry' -P '!ads' -P 'A3000' -U -s ${settings_file} -gs ${settings_file} -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -DarchetypeCatalog=internal

#Step5 定义包名路径，并将包移动到dockerfile同级目录
module_path=${CRTDIR}/${module_name}
app_path=${module_path}/target/${app}
ls -l ${app_path}
mv ${app_path} ${CRTDIR}/

#Step6 下载jre
artget pull "${cmc_repository}" -ru software -user ${CMC_USERNAME} -pwd ${CMC_PASSWORD} -rp safe_images/jre/jre-${PLATFORM}.tar -ap ./
docker load -i jre-${PLATFORM}.tar
rm -f jre-${PLATFORM}.tar
jre_base=jre:${PLATFORM}

#Step7 打包镜像
SERVICE_VERSION=${jane_branch}-${ENV_PIPELINE_STARTTIME}
echo "buildVersion=eDataMate-${ENV_PIPELINE_STARTTIME}" >> "${WORKSPACE}"/buildInfo.properties

mv EulerOS_${PLATFORM}.repo EulerOS.repo

if [ ${PLATFORM} == "arm_64" ]; then
  curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.aarch64.rpm -o seccomponent-1.1.8-release.rpm
else
  curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.x86_64.rpm -o seccomponent-1.1.8-release.rpm
fi

mkdir sql
server_sql_list=$(find "${WORKSPACE}"/Orchestration/jane/jober/sql/A3000/new/ -name "*.sql")
for i in ${server_sql_list}
do
    cp "$i" sql/
done

if [ -z "${APP_VERSION}" ];then
  docker build --build-arg BASE_IMAGE=${jre_base} --build-arg APP=${app} -t ${remote_harbor}/${ENV}/${image_name}:${PLATFORM}-${SERVICE_VERSION} --file=${CRTDIR}/Dockerfile ${CRTDIR}/
  docker save -o "${image_name}.${PLATFORM}-${SERVICE_VERSION}.tar" ${remote_harbor}/${ENV}/${image_name}:${PLATFORM}-${SERVICE_VERSION}

  cd sql
  tar -cvf sql_jane_${ENV_PIPELINE_STARTTIME}.tar *
  mv sql_jane_${ENV_PIPELINE_STARTTIME}.tar ../
else
  docker build --build-arg BASE_IMAGE=${jre_base} --build-arg APP=${app} -t fit/${image_name}:${image_tag} --file=${CRTDIR}/Dockerfile ${CRTDIR}/
  docker tag fit/edatamate-jane:${PLATFORM}-${APP_VERSION} edatamate-jane:${PLATFORM}-${APP_VERSION}
  docker save -o edatamate-jane.${PLATFORM}-${APP_VERSION}.tar edatamate-jane:${PLATFORM}-${APP_VERSION}

  cd sql
  tar -cvf sql_jane_${APP_VERSION}.tar *
  mv sql_jane_${APP_VERSION}.tar ../
fi
