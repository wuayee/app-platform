import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import {useDataContext} from "@/components/DefaultRoot.jsx";
import React from "react";
import {Collapse, Popover} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";

const {Panel} = Collapse;
/**
 * fit接口出参展示
 *
 * @returns {JSX.Element}
 */
export default function InvokeOutput() {
    const data = useDataContext();
    const outputData = data && data.outputParams;

    const getContent = () => {
        const contentItems = data.outputParams
            .filter(item => item.description)  // 过滤出有描述的项目
            .map((item) => (
                <p key={item.id}>{item.name}: {item.description}</p>
            ));

        if (contentItems.length === 0) {
            return null;  // 如果没有内容，返回null
        }

        return (
            <div className={"jade-font-size"} style={{ lineHeight: "1.2" }}>
                <p>参数介绍：</p>
                {contentItems}
            </div>
        );
    };

    const content = getContent();

    return (<Collapse bordered={false} className="jade-custom-collapse"
                      defaultActiveKey={['InvokeOutput']}>
        <Panel
            className="jade-panel"
            header={<div style={{display: 'flex', alignItems: 'center'}}>
                <span className='jade-panel-header-font'>输出</span>
                {content ? (
                    <Popover content={content}>
                        <QuestionCircleOutlined className="jade-panel-header-popover-content"/>
                    </Popover>
                ) : null}
            </div>}
            key='InvokeOutput'>
            <div className={"jade-custom-panel-content"}>
                <JadeObservableTree data={outputData}/>
            </div>
        </Panel>
    </Collapse>);
}