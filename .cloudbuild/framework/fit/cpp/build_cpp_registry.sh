#!/bin/bash

set -eu

script_file=$(readlink -f "$0")
current_dir=$(dirname "${script_file:?}")


repo_type=${1-:"memory"}
build_args=${2:-"build_grpc:true"}
fit_dir=${3:-"${current_dir}/../.."}
cpp_build_type=${4:-debug}
arch_type=${5:-x86_64}
image_full_name=${6:-""}

app="registry_server"
cpp_dir="${fit_dir}/cpp"
cpp_build_dir="${cpp_dir}/build-tmp"

if [ "${arch_type}" != "x86_64" ]; then
    arch_type="aarch64"
fi

echo "cpp_dir=${cpp_dir}"

echo "cpp_build_type=${cpp_build_type}"

cat /etc/os-release
bash "${cpp_dir}"/build.sh "${build_args},build_type:${cpp_build_type}" "${cpp_build_dir}"

# 组织运行包
app_dir="${current_dir}/package_${repo_type}/${app}"
bash "${current_dir}"/registry_server/deploy_${repo_type}/package.sh "${cpp_build_dir}" "${cpp_dir}/third_party" "${app_dir}" "${cpp_build_type}"

# 打包镜像
if [ "${image_full_name}" != "" ]; then
    docker build --build-arg APP=${app} -t ${image_full_name} --pull=true \
        --file="${current_dir}/registry_server/deploy_${repo_type}/${arch_type}/Dockerfile" "${current_dir}/package_${repo_type}/"
fi