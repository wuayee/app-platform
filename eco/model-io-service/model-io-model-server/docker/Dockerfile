FROM ubuntu:22.04 AS base

MAINTAINER zhangshijie42@huawei.com

ARG ARCH="aarch64"

# apt 源配置
RUN cp /etc/apt/sources.list /etc/apt/sources.list.bak && \
    sed -i "s@http://ports.ubuntu.com@http://mirrors.tools.huawei.com@g" /etc/apt/sources.list && \
    apt update && \
    apt install -y vim git wget curl unzip build-essential python3 pip && \
    mkdir /root/.pip && \
    echo "[global]\n\
index-url = https://mirrors.tools.huawei.com/pypi/simple\n\
trusted-host = mirrors.tools.huawei.com\n\
timeout = 120" >/root/.pip/pip.conf && \
    pip install torch==2.1.0 decorator attrs psutil absl-py cloudpickle scipy synr==0.5.0 tornado numpy==1.26.4 pandas sentencepiece accelerate thefuzz loguru tiktoken transformers --quiet 2>/dev/null && \
    mkdir -p /root/install

FROM base AS install

ARG ARCH="aarch64"

# 拷贝安装依赖
COPY ./Ascend-cann-kernels-910b_8.0.RC1_linux.run /root/install
COPY ./Ascend-cann-toolkit_8.0.RC1_linux-"${ARCH}".run /root/install
COPY ./Ascend-mindie_1.0.RC1_linux-"${ARCH}".run /root/install
COPY ./Ascend-mindie-atb-models_1.0.RC1_linux-"${ARCH}"_torch2.1.0-abi0.tar.gz /root/install
COPY ./apex-0.1_ascend_20240413-cp310-cp310-linux_"${ARCH}".whl /root/install
COPY ./torch-2.1.0-cp310-cp310-manylinux_2_17_"${ARCH}".manylinux2014_"${ARCH}".whl /root/install
COPY ./torch_npu-2.1.0.post3_20240413-cp310-cp310-manylinux_2_17_"${ARCH}".manylinux2014_"${ARCH}".whl /root/install
COPY ./pytorch_v2.1.0-6.0.rc1_py310.tar.gz /root/install
COPY ./install_and_enable_cann.sh /root/install

RUN chmod +x /root/install/Ascend-cann-kernels-910b_8.0.RC1_linux.run && \
    chmod +x /root/install/Ascend-cann-toolkit_8.0.RC1_linux-"${ARCH}".run && \
    chmod +x /root/install/Ascend-mindie_1.0.RC1_linux-"${ARCH}".run && \
    chmod +x /root/install/install_and_enable_cann.sh && \
    bash /root/install/install_and_enable_cann.sh && \
    rm -rf /root/install

FROM install AS mindie

RUN mkdir -p /root/mindie

COPY ./mindie/start_mindie.sh /root/mindie

RUN chmod +x /root/mindie/start_mindie.sh

ENTRYPOINT ["bash","-c"]
CMD ["/root/mindie/start_mindie.sh"]

FROM install AS model-io-embedding

RUN mkdir -p /root/mindie && \
    pip install typing fastapi

COPY ./embed/embedding_server.py /root/mindie
COPY ./embed/start_mindie.sh /root/mindie

ENTRYPOINT ["bash","-c"]
CMD ["/root/mindie/start_mindie.sh"]