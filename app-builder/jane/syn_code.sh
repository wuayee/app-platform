#!/bin/bash
set -ex
# 同步代码仓指定分支到目标仓库

#获取参数
y_token=$1
g_token=$2

# 代码空间目录
WORK_SPACE="../../test"
# 待同步源仓库名
REPO_NAME="jane"
# 待同步源仓git地址
SOURCE_GIT_PATH="https://oauth2:${g_token}@szv-open.codehub.huawei.com/innersource/jane_G/jane.git"
# 待同步目标仓git地址
TARGET_GIT_PATH="https://oauth2:${y_token}@codehub-dg-y.huawei.com/fitlab/jane.git"
# 当前分支
cur_branch=$(git rev-parse --abbrev-ref HEAD)

type git &>/dev/null
if test $? -ne 0; then
	echo "[ERROR] please install 'git' tool first by 'yum install git' or 'apt-get install git'"
	exit 1
fi
finish_message=""

echo "${WORK_SPACE}"

if [ ! -d ${WORK_SPACE} ]; then
	echo "[INFO] start mkdir workspace"
	mkdir -p ${WORK_SPACE}
fi
cd ${WORK_SPACE}
repo_dir="${WORK_SPACE}/${REPO_NAME}.git"

# 如果本地仓库已存在先pull
echo ${repo_dir}
if [ ! -d ${repo_dir} ]; then
	echo "[INFO] start to clone"
	git clone --mirror ${SOURCE_GIT_PATH}
else
	echo "[INFO] start to fetch"
	git fetch -p
fi

cd ${REPO_NAME}".git"

push_cmd="git push ${TARGET_GIT_PATH} --prune "
push_cmd="${push_cmd} refs/heads/${cur_branch}:refs/heads/${cur_branch} "
#if [ ${#branch_list[@]} -ne 0 ]; then
#	for ref in "${branch_list[@]}"
#	do
#		push_cmd="${push_cmd} refs/heads/${ref}:refs/heads/${ref} "
#	done
#fi

# git push https://NEW-REMOTE-HOSTNAME/path/to/repo.git --prune refs/tags/*:refs/tags/* refs/heads/*:refs/heads/*
echo "[INFO] start to execute: ${push_cmd}"
$push_cmd
if test $? -eq 0; then
	finish_message="[INFO ] push ref success"
else
	finish_message="[ERROR] push ref fail: ${result}"
fi

echo "-------------------------------------------------------------------------------------"
echo "${finish_message}"
echo "-------------------------------------------------------------------------------------"