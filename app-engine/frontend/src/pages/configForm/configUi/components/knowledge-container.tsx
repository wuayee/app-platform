/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useRef, useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setConfigItem } from '@/store/appConfig/config';
import Knowledge from './knowledge';
import { Collapse } from 'antd';
const { Panel } = Collapse;

const KnowledgeContainer = (props) => {
  const { graphOperator, config, updateData } = props;
  const [knowledge, setKnowledge] = useState([]);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const knowledgeRef: any = useRef(null);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新knowledge
  const updateKnowledge = (value) => {
    if (config.from === 'graph') {
      graphOperator.update(config.defaultValue, value);
    } else {
      dispatch(setConfigItem({ key: config.name, value }));
    }
    updateData();
  };

  // 新增知识库
  const addKnowledgeBase = (event) => {
    event.stopPropagation();
    knowledgeRef.current.addKnowledge();
    setActivePanelKey(['knowledge']);
  };

  useEffect(() => {
    if (knowledge?.length) {
      setActivePanelKey(['knowledge']);
    }
  }, [knowledge]);

  useEffect(() => {
    if (!config.from) {
      return;
    }
    if (config.from === 'graph') {
      setKnowledge(graphOperator.getConfig(config.defaultValue));
    } else {
      setKnowledge(config.defaultValue);
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
        <img src="./src/assets/images/add_btn.svg" style={{ width: 16, height: 16 }} alt="" onClick={addKnowledgeBase} />
      </div>} forceRender key='knowledge' className="site-collapse-custom-panel">
        <Knowledge knowledgeRef={knowledgeRef} knowledge={knowledge} updateData={updateKnowledge} />
      </Panel>
    </Collapse>
  </>
};

export default KnowledgeContainer;