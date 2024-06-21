#!/usr/bin/bash

NpuDeviceIds=$(seq 0 $(expr ${WorldSize} - 1) | tr '\n' ',')
NpuDeviceIds=${NpuDeviceIds%,}
if [ "${MaxTokenSize}" == "" ]; then
  MaxTokenSize=4096
fi

echo "{
    \"OtherParam\" :
    {
        \"ResourceParam\" :
        {
            \"cacheBlockSize\" : 128,
            \"preAllocBlocks\" : 4
        },
        \"LogParam\" :
        {
            \"logLevel\" : \"Info\",
            \"logPath\" : \"/logs/mindservice.log\"
        },
        \"ServeParam\" :
        {
            \"ipAddress\" : \"0.0.0.0\",
            \"port\" : ${Port},
            \"maxLinkNum\" : 300,
            \"httpsEnabled\" : false,
            \"tlsCaPath\" : \"security/ca/\",
            \"tlsCaFile\" : [\"ca.pem\"],
            \"tlsCert\" : \"security/certs/server.pem\",
            \"tlsPk\" : \"security/keys/server.key.pem\",
            \"tlsPkPwd\" : \"security/pass/mindie_server_key_pwd.txt\",
            \"kmcKsfMaster\" : \"tools/pmt/master/ksfa\",
            \"kmcKsfStandby\" : \"tools/pmt/standby/ksfb\",
            \"tlsCrl\" : \"security/certs/server_crl.pem\"
        }
    },
    \"WorkFlowParam\":
    {
        \"TemplateParam\" :
        {
            \"templateType\": \"Standard\",
            \"templateName\" : \"Standard_llama\",
            \"pipelineNumber\" : 1
        }
    },
    \"ModelDeployParam\":
    {
        \"maxSeqLen\" : ${MaxTokenSize},
        \"npuDeviceIds\" : [[${NpuDeviceIds}]],
        \"ModelParam\" : [
            {
                \"modelInstanceType\": \"Standard\",
                \"modelName\" : \"${ModelName}\",
                \"modelWeightPath\" : \"${ModelWeightPath}\",
                \"worldSize\" : ${WorldSize},
                \"cpuMemSize\" : 5,
                \"npuMemSize\" : 8,
                \"backendType\": \"atb\"
            }
        ]
    },
    \"ScheduleParam\":
    {
        \"maxPrefillBatchSize\" : 50,
        \"maxPrefillTokens\" : 8192,
        \"prefillTimeMsPerReq\" : 150,
        \"prefillPolicyType\" : 0,

        \"decodeTimeMsPerReq\" : 50,
        \"decodePolicyType\" : 0,

        \"maxBatchSize\" : 200,
        \"maxIterTimes\" : 512,
        \"maxPreemptCount\" : 200,
        \"supportSelectBatch\" : false,
        \"maxQueueDelayMicroseconds\" : 5000
    }
}" >/usr/local/Ascend/mindie/latest/mindie-service/conf/config.json
cat /usr/local/Ascend/mindie/latest/mindie-service/conf/config.json
cd /usr/local/Ascend/mindie/latest/mindie-service
export LD_LIBRARY_PATH=/usr/local/Ascend/driver/lib64/driver:/usr/local/Ascend/driver/lib64/common:/usr/local/Ascend/ascend-toolkit/latest/aarch64-linux/devlib:$LD_LIBRARY_PATH
export PYTHONPATH=/usr/local/Ascend/llm_model:$PYTHONPATH
source /usr/local/Ascend/ascend-toolkit/set_env.sh
source /usr/local/Ascend/mindie/set_env.sh
source /usr/local/Ascend/llm_model/set_env.sh
./bin/mindieservice_daemon
