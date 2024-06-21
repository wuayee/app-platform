import React from 'react';
import {Collapse} from 'antd';

const {Panel} = Collapse;

/**
 * 人工检查节点通用折叠区域组件
 *
 * @param defaultActiveKey 默认展开区域
 * @param panelKey panel的key
 * @param headerText 标题
 * @param children 子组件
 * @param panelStyle panel样式
 * @return {JSX.Element}
 * @constructor
 */
export default function JadePanelCollapse({defaultActiveKey, panelKey, headerText, children, panelStyle}) {
    return (<>
        <Collapse bordered={false} className="jade-collapse-custom-background-color"
                  defaultActiveKey={defaultActiveKey}>
            <Panel
                    key={panelKey}
                    header={
                        <div className="panel-header"
                             style={{display: 'flex', alignItems: 'center', justifyContent: "flex-start"}}>
                            <span className="jade-panel-header-font">{headerText}</span>
                        </div>
                    }
                    className="jade-panel"
                    style={panelStyle}
            >
                {children}
            </Panel>
        </Collapse>
    </>);
}


