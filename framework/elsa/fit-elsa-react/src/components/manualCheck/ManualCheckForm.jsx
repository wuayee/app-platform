import {Form} from "antd";
import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import OriginForm from "@/components/manualCheck/OriginForm.jsx";
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import {useEffect, useState} from "react";
import httpUtil from "@/components/util/httpUtil.jsx";
import JadePanelCollapse from "@/components/manualCheck/JadePanelCollapse.jsx";

/**
 * 人工检查节点折叠区域组件
 *
 * @param data  数据
 * @param handleFormChange 选项修改后的回调
 * @return {JSX.Element}
 * @constructor
 */
export default function ManualCheckForm({data, handleFormChange}) {
    const config = useShapeContext().graph.configs.find(node => node.node === "manualCheckNodeState");
    const formName = data.formName;
    const taskId = data.taskId;
    const [formOptions, setFormOptions] = useState([]);
    const selectedFormDefaultValue = (formName === null || formName === undefined) ? undefined : `${formName.replace(/Component$/, '')}|${taskId}`;
    const shape = useShapeContext();

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

    /**
     * 切换表单选项后的回调
     *
     * @param e event
     */
    const onChange = (e) => {
        let formEntity = "";
        let changeFormName = "";
        let changeFormId = "";
        if (e && e.length > 0) {
            const [name, id] = e.split('|'); // 拆分字符串
            changeFormName = name + "Component";
            changeFormId = id;
            try {
                formEntity = shape.graph.plugins[changeFormName]().getJadeConfig();
            } catch (error) {
                console.error("Error getting JadeConfig:", error);
            }
        }
        handleFormChange(changeFormName, changeFormId, formEntity);
    };

    return (<>
        <JadePanelCollapse
                defaultActiveKey={["manualCheckFormPanel"]}
                panelKey="manualCheckFormPanel"
                headerText="表单"
                panelStyle={{marginBottom: 8, borderRadius: "8px", width: "100%"}}
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
        </JadePanelCollapse>
    </>);
}