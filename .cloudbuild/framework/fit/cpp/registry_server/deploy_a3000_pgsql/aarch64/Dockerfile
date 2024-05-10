FROM euleros:aarch64

ENV RUN_APP_PROJ /opt/fit

ARG APP
ENV APP ${APP}

RUN mkdir -p ${RUN_APP_PROJ}

COPY scc.conf /
COPY EulerOS.repo /etc/yum.repos.d/
RUN yum makecache && \
    yum install -y shadow-utils && \
    groupadd -g 2000 -r edatamate && \
    useradd -u 1400 -r -g 2000 -M registry && \
    yum install -y openssl

RUN curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.aarch64.rpm -o seccomponent-1.1.8-release.rpm \
    && rpm -ivh seccomponent-1.1.8-release.rpm \
    && rm seccomponent-1.1.8-release.rpm \
    && chmod 500 /usr/local/seccomponent -R \
    && chown -R 1400:2000 /usr/local/seccomponent

COPY ${APP} ${RUN_APP_PROJ}/${APP}

RUN chown -R 1400:2000 ${RUN_APP_PROJ}/${APP}

WORKDIR ${RUN_APP_PROJ}/${APP}

RUN chmod 640 start.sh && \
    chmod 640 generate_crt.sh  && \
    chmod -R 750 bin  && \
    chmod -R 700 bin/FitWorker  && \
    chmod -R 750 conf  && \
    chmod -R 640 conf/*  && \
    chmod -R 550 lib  && \
    chmod -R 550 third_party 

CMD sh start.sh
