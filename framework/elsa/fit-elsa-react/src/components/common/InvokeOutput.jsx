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
export default function InvokeOutput() {
    const data = useDataContext();
    const outputData = data && data.outputParams;

    return (<Collapse bordered={false} className="jade-collapse-custom-background-color"
                      defaultActiveKey={['InvokeOutput']}>
        <Panel
            className="jade-panel"
            header={<div style={{display: 'flex', alignItems: 'center'}}>
                <span className='jade-panel-header-font'>输出</span>
            </div>}
            key='InvokeOutput'>
            <JadeObservableTree data={outputData}/>
        </Panel>
    </Collapse>);
}