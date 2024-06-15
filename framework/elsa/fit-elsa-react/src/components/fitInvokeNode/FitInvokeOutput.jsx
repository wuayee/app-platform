import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import {useDataContext} from "@/components/DefaultRoot.jsx";
import React from "react";
import {Collapse} from "antd";

const {Panel} = Collapse;
/**
 * fit接口出参展示
 *
 * @returns {JSX.Element}
 */
export default function FitInvokeOutput() {
    const data = useDataContext();
    const outputData = data && data.outputParams;

    return (<Collapse bordered={false} className="jade-custom-collapse"
                      defaultActiveKey={['FitInvokeOutput']}>
        <Panel
            className="jade-panel"
            header={<div style={{display: 'flex', alignItems: 'center'}}>
                <span className={'title'}>输出</span>
            </div>}
            key='FitInvokeOutput'>
            <div className={"jade-custom-panel-content"}>
                <JadeObservableTree data={outputData}/>
            </div>
        </Panel>
    </Collapse>);
}