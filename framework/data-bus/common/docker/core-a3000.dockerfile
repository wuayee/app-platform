ARG BASE
FROM ${BASE}
WORKDIR /databus
EXPOSE 5284
ADD databus /databus
ADD a3000_init.sh /databus

COPY EulerOS.repo /etc/yum.repos.d/
RUN yum makecache && \
    yum install -y glibc libstdc++ logrotate cronie shadow-utils && \
    groupadd -g 2000 -r edatamate && \
    useradd -u 1300 -r -g 2000 -M runtime && \
    cp /etc/cron.daily/logrotate /etc/cron.hourly/ && \
    chmod 500 /databus/a3000_init.sh && \
    chmod 500 /databus/databus && \
    rm -f /etc/yum.repos.d/EulerOS.repo

ENTRYPOINT ["/databus/a3000_init.sh"]