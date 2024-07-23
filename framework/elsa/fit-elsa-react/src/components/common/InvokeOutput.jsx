import {JadeObservableTree} from "@/components/common/JadeObservableTree.jsx";
import React from "react";
import {Collapse, Popover} from "antd";
import {QuestionCircleOutlined} from "@ant-design/icons";
import PropTypes from "prop-types";
import ArrayUtil from "@/components/util/ArrayUtil.js";

const {Panel} = Collapse;

_InvokeOutput.propTypes = {
    outputData: PropTypes.array
};

/**
 * fit接口出参展示
 *
 * @param outputData 输出数据
 * @returns {JSX.Element}
 */
function _InvokeOutput({outputData}) {

    const getContent = () => {
        const contentItems = outputData
                .filter(item => item.description)  // 过滤出有描述的项目
                .map((item) => (
                        <p key={item.id}>{item.name}: {item.description}</p>
                ));

        if (contentItems.length === 0) {
            return null;  // 如果没有内容，返回null
        }

        return (<>
            <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
                <p>参数介绍：</p>
                {contentItems}
            </div>
        </>);
    };

    const content = getContent();

    return (<>
        <Collapse bordered={false} className="jade-custom-collapse"
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
        </Collapse>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return ArrayUtil.isEqual(prevProps.outputData, nextProps.outputData);
};

export const InvokeOutput = React.memo(_InvokeOutput, areEqual);