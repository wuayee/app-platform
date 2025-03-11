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
import { Message } from '@/shared/utils/message';
import CloseImg from '@/assets/images/close_arrow.png';
import OpenImg from '@/assets/images/open_arrow.png';
import AddImg from '@/assets/images/add_btn.svg';
const { Panel } = Collapse;

const KnowledgeContainer = (props) => {
  const { graphOperator, config, updateData, validateList } = props;
  const [knowledge, setKnowledge] = useState([]);
  const [activePanelKey, setActivePanelKey] = useState(['']);
  const knowledgeRef: any = useRef(null);
  const curKnowledge = useRef(null);
  const dispatch = useAppDispatch();
  const appConfig = useAppSelector((state) => state.appConfigStore.inputConfigData);

  // 更新knowledge
  const updateKnowledge = (value) => {
    value.forEach(item => delete item.notExist);
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

  // 更新每一条是否存在
  const updateExistStatus = () => {
    if (!validateList?.length) {
      return;
    }
    curKnowledge.current = curKnowledge.current.map(item => {
      return {
        ...item,
        notExist: !!validateList.find(it => it.configName === 'knowledgeRepos' && it.id == item.id),
      }
    });
    setKnowledge(curKnowledge.current);
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
      try {
        curKnowledge.current = graphOperator.getConfig(config.defaultValue);
      } catch {
        Message({ type: 'warning', content: '能力配置数据与elsa数据节点不匹配' });
      }
    } else {
      curKnowledge.current = config.defaultValue;
    }
    setKnowledge(curKnowledge.current);
    updateExistStatus();
  }, [config, appConfig]);

  useEffect(() => {
    updateExistStatus();
  }, [validateList]);
  
  return <>
    <Collapse
      bordered={false}
      expandIcon={({ isActive }) => isActive ? <img src={CloseImg} alt="" /> : <img src={OpenImg} alt="" />}
      activeKey={activePanelKey}
      onChange={(keys) => setActivePanelKey(keys)}
    >
      <Panel header={<div className='panel-label'>
        <span>{config.description}</span>
        <img src={AddImg} style={{ width: 16, height: 16 }} alt="" onClick={addKnowledgeBase} />
      </div>} forceRender key='knowledge' className="site-collapse-custom-panel">
        <Knowledge knowledgeRef={knowledgeRef} knowledge={knowledge} updateData={updateKnowledge} />
      </Panel>
    </Collapse>
  </>
};

export default KnowledgeContainer;