FROM euleros:x86_64

ENV RUN_APP_PROJ /opt/fit

ARG APP
ENV APP ${APP}

RUN mkdir -p ${RUN_APP_PROJ}

COPY ${APP} ${RUN_APP_PROJ}/${APP}

WORKDIR ${RUN_APP_PROJ}/${APP}

CMD sh start.sh
