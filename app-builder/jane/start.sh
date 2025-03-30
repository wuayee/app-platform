#!/bin/sh
set -ex

execute_time=`date`
echo "/***********************************************************************"
echo " *"
echo " *  Start Jane Jober Service"
echo " *  Execute Date : $execute_time"
echo " *"
echo " ***********************************************************************/"

echo ""
echo "Launch Application..."

export REST_PORT=8003
export DSF_PORT=7003

cd /opt/jane/fitframework
echo $(ls -l)
chmod 755 -R /opt/jane/fitframework/bin/fit
cd /opt/jane/custom
echo $(ls -l)

MAPPING_PORT=$(echo ${VMPORT} | awk -F ',' '{ for (i=1;i<=NF;i++) printf "%s\n",$i }' | grep public -A 2 | grep HostPort | awk -F '[:"]' '{print $5}')
HTTP_HOST=${VMIP}
FIT_WORKER_ID="jober-worker-${HTTP_HOST}-${MAPPING_PORT}"

/opt/jane/fitframework/bin/fit start $JAVA_OPTS worker.host=${HTTP_HOST} server.http.to-register-port=${MAPPING_PORT} worker.id=${FIT_WORKER_ID} -XX:+UseG1GC plugin.fit.dynamic.plugin.repository-url=https://repo.maven.apache.org/maven2 -Dlog4j2.configurationFile=./log4j2.xml
echo "Application started..."