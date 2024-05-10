#!/bin/bash

set -eu

script_file=$(readlink -f "$0")
current_dir=$(dirname "${script_file:?}")
build_type=${1:-debug}
arch_type=${2:-x86_64}
base_image_file=$3

fit_dir=${4:-"${current_dir}/../.."}
cpp_dir="${fit_dir}/cpp"

output_dir=${5:-"${fit_dir}/output"}

SERVICE_VERSION=cpp-${ENV_PIPELINE_STARTTIME}
APP_VERSION=${APP_VERSION:-"${ENV_PIPELINE_STARTTIME}"}

# 获取服务参数
image_name="edatamate-fit-registry"
if [ -z "${APP_VERSION}" ];then
  tag_name="${PLATFORM}-${SERVICE_VERSION}"
else
  tag_name="${PLATFORM}-${APP_VERSION}"
fi

mkdir -p "${output_dir}"

if [ "${arch_type}" != "x86_64" ]; then
    arch_type="aarch64"
    curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.aarch64.rpm -o seccomponent-1.1.8-release.rpm
else
    curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.x86_64.rpm -o seccomponent-1.1.8-release.rpm
fi
rpm -ivh seccomponent-1.1.8-release.rpm
rm seccomponent-1.1.8-release.rpm

cpp_build_type=$([ ${build_type} == "release" ] && echo "release" || echo "debug")
echo "cpp_dir=${cpp_dir},cpp_build_type=${cpp_build_type},arch_type=${arch_type},image_name=${image_name},tag_name=${tag_name}"

# import base image
base_image_arch_name="${arch_type}"
docker import "${base_image_file}"/$(ls ${base_image_file} | grep docker) euleros:${base_image_arch_name}

deploy_target="a3000_pgsql"
bash "${current_dir}"/build_cpp_registry.sh "${deploy_target}" "build_ssl:true,build_libpq:true" "${fit_dir}" "${cpp_build_type}" "${arch_type}"

echo "buildVersion=eDataMate-${ENV_PIPELINE_STARTTIME}" >> "${WORKSPACE}"/buildInfo.properties


cp ${current_dir}/registry_server/deploy_${deploy_target}/${arch_type}/EulerOS.repo ${current_dir}/package_${deploy_target}/
cp ${current_dir}/registry_server/deploy_${deploy_target}/${arch_type}/scc.conf ${current_dir}/package_${deploy_target}/
docker build --build-arg APP=registry_server -t ${image_name}:${tag_name} --pull=false \
        --file="${current_dir}/registry_server/deploy_${deploy_target}/${arch_type}/Dockerfile" "${current_dir}/package_${deploy_target}/"

docker save -o "${output_dir}/${image_name}.${tag_name}.tar" "${image_name}:${tag_name}"

# tar sql
mkdir sql
registry_sql_list=(
    ${cpp_dir}/plugin/registry_server_repository_pg/sql/02_create_fit_registry_table.sql
)
for i in ${registry_sql_list}
do
  cp "$i" sql/
done
cd sql
tar -cvf sql_registry_${APP_VERSION}.tar *
mv sql_registry_${APP_VERSION}.tar "${output_dir}"
