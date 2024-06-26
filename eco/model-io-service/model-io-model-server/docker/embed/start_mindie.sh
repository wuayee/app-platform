#!/usr/bin/bash
export LD_LIBRARY_PATH=/usr/local/Ascend/driver/lib64/driver:/usr/local/Ascend/driver/lib64/common:/usr/local/Ascend/ascend-toolkit/latest/aarch64-linux/devlib:$LD_LIBRARY_PATH
export PYTHONPATH=/usr/local/Ascend/llm_model:$PYTHONPATH

source /usr/local/Ascend/ascend-toolkit/set_env.sh
source /usr/local/Ascend/mindie/set_env.sh
source /usr/local/Ascend/llm_model/set_env.sh
python3 /root/mindie/embedding_server.py
