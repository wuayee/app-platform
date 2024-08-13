FROM ubuntu:jammy
WORKDIR /databus
EXPOSE 5284
ADD databus /databus
ADD core_init.sh /databus

# 安装 logrotate
RUN rm -f /etc/apt/sources.list && \
    echo "deb http://mirrors.tools.huawei.com/ubuntu jammy main restricted" >> /etc/apt/sources.list && \
    echo "deb http://mirrors.tools.huawei.com/ubuntu jammy-updates main restricted" >> /etc/apt/sources.list && \
    apt-get update &&  \
    apt-get -y install logrotate && \
    groupadd -g 2000 -r edatamate && \
    useradd -u 1300 -r -g 2000 -M runtime && \
    chmod 500 /databus/core_init.sh && \
    chmod 500 /databus/databus

ENTRYPOINT ["/databus/core_init.sh"]