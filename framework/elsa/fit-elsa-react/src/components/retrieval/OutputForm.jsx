import {Collapse, Popover, Form} from "antd";
import {InfoCircleOutlined} from "@ant-design/icons";
import React from "react";
import "./style.css";
import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import {useDataContext, useShapeContext} from "@/components/DefaultRoot.jsx";

const {Panel} = Collapse;

/**
 * 内容输出组件
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function OutputForm() {
    const data = useDataContext();
    const shape = useShapeContext();
    const outputData = data && data.outputParams;

    const tips = <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
        <p>输出列表是与输入参数最匹配的信息，从所有选定的知识库中调用</p>
    </div>;

    return (
        <Collapse
            bordered={false} className="jade-collapse-custom-background-color"
            style={{marginTop: "10px", marginBottom: 8, borderRadius: "8px", width: "100%"}}
            defaultActiveKey={['Output']}>
            <Panel
                header={
                    <div
                        style={{display: 'flex', alignItems: 'center', paddingLeft: '-16px'}}>
                        <span className="jade-panel-header-font">输出</span>
                        <Popover content={tips}>
                            <InfoCircleOutlined className="jade-panel-header-popover-content"/>
                        </Popover>
                    </div>
                }
                className="jade-panel"
                key='Output'
            >
                <JadeObservableTree data={outputData}/>
            </Panel>
        </Collapse>
    )
}