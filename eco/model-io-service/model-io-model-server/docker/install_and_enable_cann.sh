#!/usr/bin/bash

# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Install Torch, Torch_npu, Apex
WORK_PATH=$(dirname "$0")

pip3 install "${WORK_PATH}/torch-2.1.0-cp310-cp310-manylinux_2_17_$(arch).manylinux2014_$(arch).whl" --quiet 2>/dev/null;

PYTORCH_MANYLINUX="${WORK_PATH}"/pytorch_v2.1.0-6.0.rc1_py310.tar.gz
TORCH_NPU_IN_PYTORCH_MANYLINUX="torch_npu-2.1.0.post3_20240413-cp310-cp310-manylinux_2_17_$(arch).manylinux2014_$(arch).whl"
APEX_IN_PYTORCH_MANYLINUX="apex-0.1_ascend_20240413-cp310-cp310-linux_$(arch).whl"
mkdir -p "${WORK_PATH}"/torch
tar -zxvf "${PYTORCH_MANYLINUX}" -C "${WORK_PATH}"/torch

echo "start install pytorch, wait for a minute..."
if pip3 install "${WORK_PATH}/torch/${TORCH_NPU_IN_PYTORCH_MANYLINUX}" --quiet 2>/dev/null; then
  echo "pip3 install torchnpu successfully"
else
  echo "pip3 install torchnpu failed"
fi

if pip3 install "${WORK_PATH}/torch/${APEX_IN_PYTORCH_MANYLINUX}" --quiet 2>/dev/null; then
  echo "pip3 install apex successfully"
else
  echo "pip3 install apex failed"
fi
rm -rf torch

# Install Ascend Cann Library
CANN_TOOKIT="${WORK_PATH}/Ascend-cann-toolkit_8.0.RC1_linux-$(arch).run"
CANN_KERNELS="${WORK_PATH}/Ascend-cann-kernels-910b_8.0.RC1_linux.run"
chmod +x "${CANN_TOOKIT}" "${CANN_KERNELS}"

if yes | "${CANN_TOOKIT}" --install --quiet; then
  echo "install toolkit successfully"
else
  echo "install toolkit failed with status $?"
fi

if yes | "${CANN_KERNELS}" --install --quiet; then
  echo "install kernels successfully"
else
  echo "install kernels failed with status $?"
fi

source /usr/local/Ascend/ascend-toolkit/set_env.sh

# Install Atb and Model
if [ ! -d "/home/llm_model" ]; then
  rm -rf /home/llm_model
fi

MINDIE="${WORK_PATH}/Ascend-mindie_1.0.RC1_linux-$(arch).run"
MODEL="${WORK_PATH}/Ascend-mindie-atb-models_1.0.RC1_linux-$(arch)_torch2.1.0-abi0.tar.gz"
mkdir -p /usr/local/Ascend/llm_model
tar -zxvf "${MODEL}" -C /usr/local/Ascend/llm_model

if yes | "${MINDIE}" --install --quiet 2>/dev/null; then
  echo "install atb successfully"
else
  echo "install atb failed with status $?"
fi
