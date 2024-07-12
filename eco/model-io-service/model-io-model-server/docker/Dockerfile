FROM euleros:dev AS base

MAINTAINER zhangshijie42@huawei.com

ARG ARCH="aarch64"

COPY Python/3.9.11/Python-3.9.11.tgz /
COPY pip/24.0/pip-24.0-py3-none-any.whl /
RUN tar -xzf /Python-3.9.11.tgz && \
    cd /Python-3.9.11 && \
    ./configure --enable-shared CFLAGS=-fPIC --enable-loadable-sqlite-extensions --prefix=/usr/local/python3 && \
    make -j8 --quiet && \
    make install --quiet
RUN pip3 install pip-24.0-py3-none-any.whl

ENV PYTHON_HOME=/usr/local/python3
ENV PATH=$PYTHON_HOME/bin:$PATH
ENV LD_LIBRARY_PATH=$PYTHON_HOME/lib:$LD_LIBRARY_PATH

RUN pip3 config set global.index-url https://cmc.centralrepo.rnd.huawei.com/artifactory/pypi-central-repo/simple/ && \
    pip3 config set global.trusted-host cmc.centralrepo.rnd.huawei.com && \
    pip3 install torch==2.1.0 && \
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

FROM euleros:base AS dev

ENV PYTHON_HOME=/usr/local/python3
ENV PATH=$PYTHON_HOME/bin:$PATH
ENV LD_LIBRARY_PATH=$PYTHON_HOME/lib:/usr/local/python3/lib/python3.9/site-packages/torch.libs:$LD_LIBRARY_PATH

COPY --from=install /usr/local/python3 /usr/local/python3
COPY --from=install /usr/local/Ascend /usr/local/Ascend

FROM dev AS mindie

RUN mkdir -p /root/mindie

COPY ./mindie/start_mindie.sh /root/mindie

RUN chmod +x /root/mindie/start_mindie.sh

ENTRYPOINT ["bash","-c"]
CMD ["/root/mindie/start_mindie.sh"]

FROM dev AS model-io-embedding

RUN mkdir -p /root/mindie && \
    pip install typing fastapi

COPY ./embed/embedding_server.py /root/mindie
COPY ./embed/start_mindie.sh /root/mindie

ENTRYPOINT ["bash","-c"]
CMD ["/root/mindie/start_mindie.sh"]