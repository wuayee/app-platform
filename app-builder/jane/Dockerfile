FROM TODO
ENV APP_PATH /opt/jane

#镜像仓里的
RUN mkdir $APP_PATH

COPY start.sh $APP_PATH
COPY fit/ $APP_PATH
COPY fit-start/src/main/resources/application-alpha.yml $APP_PATH/fitframework/
COPY fit-start/src/main/resources/application-beta.yml $APP_PATH/fitframework/
COPY fit-start/src/main/resources/application-prod.yml $APP_PATH/fitframework/
COPY fit-start/src/main/resources/log4j2.xml $APP_PATH/fitframework/
RUN chmod +x $APP_PATH/start.sh

WORKDIR $APP_PATH
RUN ls -l

# /script/start.sh来自基础镜像
CMD sh /script/start.sh $APP_PATH/start.sh