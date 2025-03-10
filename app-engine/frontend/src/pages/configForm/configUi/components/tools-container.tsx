/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import Skill from './skill';
import { Collapse } from 'antd';
const { Panel } = Collapse;

const ToolsContainer = (props) => {
  const { graphOperator, config, updateData, validateList } = props;
  const [pluginData, setPluginData] = useState([]);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const toolsRef = useRef<any>(null);
  const configFromMap = useRef<any>({});
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新工具和工作流
  const updateTools = (value, key) => {
    if (config.from === 'graph') {
      graphOperator.update(configFromMap.current[key], value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 新增插件
  const addPlugin = (event) => {
    event.stopPropagation();
    toolsRef.current.addPlugin();
    setActivePanelKey(['tools']);
  };

  useEffect(() => {
    if (pluginData?.length) {
      setActivePanelKey(['tools']);
    }
  }, [pluginData]);
  
  useEffect(() => {
    if (!config.from) {
      return;
    } 
    if (config.from === 'graph') {
      // 工具和工作流需要单独处理
      if (Array.isArray(config.defaultValue) && Array.isArray(config.defaultValue[0])) {
        let list = [];
        config.defaultValue.forEach(it => {
          configFromMap.current[it[1]] = it;
          list = [...list, ...graphOperator.getConfig(it)];
        })
        setPluginData(list);
      }
    } else {
      setPluginData(config.defaultValue);
    }
  }, [config, appConfig]);

  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src="./src/assets/images/close_arrow.png" alt="" /> : <img src="./src/assets/images/open_arrow.png" alt="" />}
      activeKey={activePanelKey}
      onChange={(keys) => setActivePanelKey(keys)}
    >
      <Panel header={<div className='panel-label'>
        <span>{config.description}</span>
        <img src="./src/assets/images/add_btn.svg" style={{ width: 16, height: 16 }} alt="" onClick={addPlugin} />
      </div>} forceRender key='tools' className="site-collapse-custom-panel">
        <Skill toolsRef={toolsRef} pluginData={pluginData} updateData={updateTools} validateList={validateList}/>
      </Panel>
    </Collapse>
  </>
};

export default ToolsContainer;