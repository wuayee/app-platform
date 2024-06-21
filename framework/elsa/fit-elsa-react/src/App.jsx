import {useEffect} from "react";
import {JadeFlow} from "./flow/jadeFlowEntry.jsx";
import {graphData} from "./testFlowData.js";
import {Button} from "antd";

function App() {
    useEffect(() => {
        const stage = document.getElementById("stage");
        const configs = [];
        configs.push({
            node: "startNodeStart",
            urls: {customHistoryUrl: "https://jane-beta.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"}
        });
        configs.push({
            node: "llmNodeState",
            urls: {
                llmModelEndpoint: "https://tzaip-beta.paas.huawei.com",
                toolListEndpoint: "https://jane-beta.huawei.com",
                workflowListEndpoint: "https://jane-beta.huawei.com"
            },
            params: {
                tenantId: "8cc048c225a04bfa8b9ab159ba09bb38",
                appId: "688c336ae9d04d479cf06e7011c34ea4"
            }
        });
        configs.push({
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge"}
        });
        configs.push({
            node: "fitInvokeState",
            urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
        });
        configs.push({node: "manualCheckNodeState", urls: {runtimeFormUrl: "https://jane-beta.huawei.com/api/jober/v1/api/8cc048c225a04bfa8b9ab159ba09bb38/form/type/runtime"}});

        // JadeFlow.new(stage, configs).then(agent => {
        //     window.agent = agent;
        //     window.agent.onChange((graphData) => {
        //         console.log(graphData);
        //         console.log("222222222222222222222");
        //     });
        // });

        JadeFlow.edit(stage, graphData, configs).then(agent => {
            window.agent = agent;
            agent.onModelSelect((onModelSelectedCallback) => {
                onModelSelectedCallback({name: "zy-model"});
            });
            agent.onChange(() => {
                // const data = agent.serialize();
                // console.log(data);
            });
        });
    });

    return (<>
        <div>
            <Button onClick={() => {
                window.agent.validate().then(() => {
                    console.log("success");
                }).catch((error) => {
                    console.log("异常: ", error);
                })
            }}>validate</Button>
            <div id="stage" style={{position: "relative", width: 1600, height: 800}}></div>
        </div>
    </>)
}

export default App
