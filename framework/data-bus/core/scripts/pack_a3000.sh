#!/usr/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core source build release script for a3000 project

set -ex
SERVICE_VERSION=databus-${ENV_PIPELINE_STARTTIME}
image_name="edatamate-databus-core"
if [ -z "${APP_VERSION}" ];then
  tag_name="${PLATFORM}-${SERVICE_VERSION}"
else
  tag_name="${PLATFORM}-${APP_VERSION}"
fi
echo "buildVersion=eDataMate-${ENV_PIPELINE_STARTTIME}">"${WORKSPACE}/buildInfo.properties"
DIR_SCRIPTS=$(readlink -f "$(dirname "$0")")
DIR_ROOT=$(readlink -f "${DIR_SCRIPTS}/..")
DIR_OUTPUT=$(readlink -f "${DIR_ROOT}/Output")
DOCKERFILE_SRC=$(readlink -f "${DIR_ROOT}/../common/docker/core-a3000.dockerfile")
LOGROTATE_INIT_SCRIPT=$(readlink -f "${DIR_SCRIPTS}/a3000_init.sh")
DOCKERFILE_DST="${DIR_OUTPUT}/core-a3000.dockerfile"
REPO_FILE_SRC=$(readlink -f "${DIR_ROOT}/../common/docker/EulerOS_${PLATFORM}.repo")
REPO_FILE_DST="${DIR_OUTPUT}/EulerOS.repo"

if [ ! -d "${DIR_OUTPUT}" ]; then
  mkdir -p "${DIR_OUTPUT}"
fi
cd "${DIR_OUTPUT}" || exit 1;

if [ "${PLATFORM}" == "arm_64" ]; then
  os_cmc="EulerOSServerV2R9C10ARM 2024.03.05.110514"
  docker_stack="Software/aarch64/DockerStack/EulerOS_Server_V200R009C10SPC230B030-docker.aarch64.tar.xz"
  devel_tools="Software/aarch64/CompileTools/devel_tools.tar.gz"
else
  os_cmc="EulerOSServerV2R9C10X86 2024.03.05.110514"
  docker_stack="Software/x86_64/DockerStack/EulerOS_Server_V200R009C10SPC230B030-docker.x86_64.tar.xz"
  devel_tools="Software/x86_64/CompileTools/devel_tools.tar.gz"
fi

#下载导入euler v2r11基础镜像
artget pull "${os_cmc}" -ru software -user "${CMC_USERNAME}" -pwd "${CMC_PASSWORD}" -rp "${docker_stack}" -ap "./"
file_name=$(basename ./*.tar.xz)
docker import "$file_name" euleros:base
base_image=euleros:base

# 编译databus core
chmod +x "${DIR_SCRIPTS}/generate_flatbuffers.sh"
source "${DIR_SCRIPTS}/build_all.sh"
exec_build_all "${DIR_OUTPUT}" "${DIR_ROOT}" "Release"

cp -r "${DOCKERFILE_SRC}" "${DOCKERFILE_DST}"
cp -r "${REPO_FILE_SRC}" "${REPO_FILE_DST}"
cp -r "${DIR_OUTPUT}/bin/databus" "${DIR_OUTPUT}/databus"
cp -r "${LOGROTATE_INIT_SCRIPT}" "${DIR_OUTPUT}/a3000_init.sh"

docker build -f "${DOCKERFILE_DST}" --build-arg BASE="${base_image}" -t "${image_name}:${tag_name}" .
docker save -o "${image_name}.${tag_name}.tar" "${image_name}:${tag_name}"