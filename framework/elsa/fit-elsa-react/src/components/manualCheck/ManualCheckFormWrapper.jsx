import OriginForm from "./OriginForm.jsx";
import "./style.css";
import {useDataContext, useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {Collapse, Form} from "antd";
import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import {useEffect, useState} from "react";
import httpUtil from "@/components/util/httpUtil.jsx";

const {Panel} = Collapse;

/**
 * 人工检查表单Wrapper
 *
 * @returns {JSX.Element} 人工检查表单Wrapper的DOM
 */
export default function ManualCheckFormWrapper() {
    const shape = useShapeContext();
    const dispatch = useDispatch();
    const data = useDataContext();
    const config = useShapeContext().graph.configs.find(node => node.node === "manualCheckNodeState");
    const formName = data.formName;
    const taskId = data.taskId;
    const output = data.outputParams;
    const [formOptions, setFormOptions] = useState([]);
    const selectedFormDefaultValue = (formName === null || formName === undefined) ? undefined : `${formName.replace(/Component$/, '')} | ${taskId}`;

    useEffect(() => {
        // 发起网络请求获取 options 数据
        httpUtil.get(config.urls.runtimeFormUrl, {}, (jsonData) => setFormOptions(jsonData.data.map(item => {
            return {
                label: item.name,
                value: `${item.appearance[0]?.name || ''}|${item.id}`
            };
        })))
    }, []); // useEffect 依赖数组为空，表示只在组件挂载时执行一次

    // 根据不同的值渲染不同的组件
    const renderComponent = () => {
        if (formName && formName.length > 0) {
            return shape.graph.plugins[formName]().getReactComponents();
        }
        return <OriginForm/>;
    };

    const onChange = (e) => {
        let formOutput = "";
        let changeFormName = "";
        let changeFormId = "";
        if (e && e.length > 0) {
            const [name, id] = e.split('|'); // 拆分字符串
            changeFormName = name + "Component";
            changeFormId = id;
            try {
                formOutput = shape.graph.plugins[changeFormName]().getJadeConfig();
            } catch (error) {
                console.error("Error getting JadeConfig:", error);
            }
        }
        dispatch({actionType: "changeFormAndSetOutput", formName: changeFormName, formId: changeFormId, formOutput: formOutput});
    };

    const renderOutput = () => {
        if (!output || !Array.isArray(output) || !output.length > 0) {
            return null;
        }
        return <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["manualCheckOutputPanel"]}>
            {
                <Panel
                    key={"manualCheckOutputPanel"}
                    header={
                        <div className="panel-header"
                             style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                            <span className="jade-panel-header-font">输出</span>
                        </div>
                    }
                    className="jade-panel"
                >
                    <div className={"jade-custom-panel-content"}>
                        <JadeObservableTree data={output}/>
                    </div>
                </Panel>
            }
        </Collapse>;
    };

    return (
        <div>
            <Collapse bordered={false} className="jade-custom-collapse" defaultActiveKey={["manualCheckFormPanel"]}>
                {
                    <Panel
                        key={"manualCheckFormPanel"}
                        header={
                            <div className="panel-header"
                                 style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                                <span className="jade-panel-header-font">表单</span>
                            </div>
                        }
                        className="jade-panel"
                        style={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
                    >
                        <div className={"jade-custom-panel-content"}>
                            <Form.Item>
                                <JadeStopPropagationSelect
                                    allowClear
                                    className="jade-select"
                                    defaultValue={selectedFormDefaultValue}
                                    style={{width: "100%", marginBottom: "8px"}}
                                    onChange={e => onChange(e)}
                                    options={formOptions}
                                />
                                {renderComponent()} {/* 渲染对应的组件 */}
                            </Form.Item>
                        </div>
                    </Panel>
                }
            </Collapse>
            {renderOutput()}
        </div>
    );
}