/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React from 'react';
import {Button, Collapse} from 'antd';
import {PlusOutlined} from '@ant-design/icons';

const {Panel} = Collapse;

/**
 * 人工检查节点通用折叠区域组件
 *
 * @param defaultActiveKey 默认展开区域
 * @param panelKey panel的key
 * @param headerText 标题
 * @param children 子组件
 * @param panelStyle panel样式
 * @param disabled 禁用状态.
 * @param triggerSelect 选择的触发器.
 * @return {JSX.Element}
 * @constructor
 */
const JadePanelCollapse = ({defaultActiveKey, panelKey, headerText, children, panelStyle, disabled, triggerSelect}) => {
    return (<>
        <Collapse bordered={false} className="jade-collapse-custom-background-color"
                  defaultActiveKey={defaultActiveKey}>
            <Panel
                    key={panelKey}
                    header={
                      <div
                        className='panel-header'
                        style={{display: 'flex', alignItems: 'center', justifyContent: 'flex-start'}}>
                        <span className='jade-panel-header-font'>{headerText}</span>
                        <Button
                          disabled={disabled}
                          type='text' className='icon-button jade-panel-header-icon-position'
                          onClick={(event) => triggerSelect(event)}>
                          <PlusOutlined/>
                        </Button>
                      </div>
                    }
                    className="jade-panel"
                    style={panelStyle}
            >
                {children}
            </Panel>
        </Collapse>
    </>);
};

export default JadePanelCollapse;