import {useEffect, useState} from "react";
import {JadeFlow} from "./flow/jadeFlowEntry.jsx";
import {graphData} from "./testFlowData.js";
import {Button} from "antd";
import {CodeDrawer} from "@/components/common/code/CodeDrawer.jsx";

function App() {
    useEffect(() => {
        const stage = document.getElementById("stage");
        const configs = [];
        configs.push({
            node: "startNodeStart",
            urls: {customHistoryUrl: "https://jane-beta.huawei.com/api/jober/v1/api/public/genericables/68dc66a6185cf64c801e55c97fc500e4?limit=10&offset=0"}
        });
        configs.push({
            node: "llmNodeState", urls: {
                llmModelEndpoint: "https://tzaip-beta.paas.huawei.com",
                toolListEndpoint: "https://jane-beta.huawei.com",
                workflowListEndpoint: "https://jane-beta.huawei.com"
            }, params: {
                tenantId: "8cc048c225a04bfa8b9ab159ba09bb38", appId: "688c336ae9d04d479cf06e7011c34ea4"
            }
        });
        configs.push({
            node: "knowledgeState",
            urls: {knowledgeUrl: "https://jane-beta.huawei.com/api/jober/v1/api/727d7157b3d24209aefd59eb7d1c49ff/knowledge"}
        });
        configs.push({
            node: "fitInvokeState", urls: {
                serviceListEndpoint: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables",
                fitableMetaInfoUrl: "https://jane-beta.huawei.com/api/jober/store/platform/tianzhou/fit/tool/genericables/"
            }
        });
        configs.push({
            node: "manualCheckNodeState",
            urls: {runtimeFormUrl: "https://jane-beta.huawei.com/api/jober/v1/api/8cc048c225a04bfa8b9ab159ba09bb38/form/type/runtime"}
        });
        configs.push({
            node: "codeNodeState", urls: {
                testCodeUrl: "https://localhost:8080/fit/CodeNode.tool/Python_REPL_TEST",
            }
        });
        configs.push({
            node: "evaluationAlgorithmsNodeState", urls: {
                datasetUrlPrefix: "http://10.245.113.7:8080/eval/",
            }
        });

        JadeFlow.edit(stage, "1111", graphData, configs).then(agent => {
            window.agent = agent;
            agent.onModelSelect((onModelSelectedCallback) => {
                onModelSelectedCallback.onSelect({name: "zy-model"});
            });
            agent.onChange((dirtyAction) => {
                console.log("dirty action: ", dirtyAction);
            });
        });
    });
    const [open, setOpen] = useState(false);

    return (<>
        <div>
            <div>
                <Button onClick={() => {
                    window.agent.validate().then(() => {
                        console.log("success");
                    }).catch((error) => {
                        console.log("异常: ", error);
                    })
                }}>validate</Button>
                <Button onClick={() => {
                    setOpen(true);
                }}>打开drawer</Button>
            </div>
            {/*<div id={"stageContainer"} style={{position: "relative"}}>*/}
            <div id="stage" style={{position: "relative", width: 1600, height: 800}}></div>
            {/*</div>*/}
            <CodeDrawer container={document.getElementById("stage")}
                        width={1232}
                        open={open}
                        languages={["python"]}
                        editorConfig={{
                            language: "python", code: "async def main(args: Args) -> Output:\n return ret"
                        }}
                        onClose={() => setOpen(false)}
                        onConfirm={(v) => {
                            // 这里对编辑后的代码进行处理
                            console.log("confirm ============ :", v)
                        }}
                        executeFunc={(args, language, callback) => {
                            console.log("execute: ", args)
                            // 这里调用执行代码的接口

                            // 接口返回的output通过callback传递给组件，展示output
                        }}/>
        </div>
    </>)
}

export default App
