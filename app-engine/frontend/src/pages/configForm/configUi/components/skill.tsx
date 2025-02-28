/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useState, useEffect, useRef, useImperativeHandle } from 'react';
import { useParams } from 'react-router-dom';
import { getMyPlugin } from '@/shared/http/plugin';
import AddSkill from '../../../addFlow/components/tool-modal';
import SkillList from './skill-list';

const Skill = (props) => {
  const { pluginData, updateData, toolsRef } = props;
  const [skillList, setSkillList] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const { tenantId } = useParams();
  const pluginMap = useRef([]);

  useImperativeHandle(toolsRef, () => {
    return { addPlugin };
  });

  const addPlugin = () => {
    setShowModal(true);
  }
  // 选择数据后回调
  const confirmCallBack = (workFlowId, fitId) => {
    if (workFlowId.length === 0 && fitId.length === 0) {
      setSkillList([]);
    }
    updateData(fitId, 'tools');
    updateData(workFlowId, 'workflows');
  }
  // 删除
  const deleteItem = (item) => {
    let workFlowList = [];
    let fitList = [];
    pluginMap.current = pluginMap.current.filter(pItem => pItem.uniqueName !== item.uniqueName);
    pluginMap.current.forEach(item => {
      if (item.tags.includes('WATERFLOW')) {
        workFlowList.push(item);
      } else {
        fitList.push(item);
      }
    });
    setSkillList([...pluginMap.current]);
    let workFlowId = workFlowList.map(item => item.uniqueName);
    let fitId = fitList.map(item => item.uniqueName);
    confirmCallBack(workFlowId, fitId);
  }
  // 获取插件列表
  const getPluginList = () => {
    getMyPlugin(tenantId, {
      pageNum: 1,
      pageSize: 100,
      tag: 'FIT',
      isDeployed: true,
    }).then(({ data }) => {
      setSkillArr(data?.pluginToolData || []);
    });
  };
  // 回显设置
  const setSkillArr = (data) => {
    pluginMap.current = [];
    data.forEach(item => {
      if (pluginData.includes(item.uniqueName)) {
        let obj = {
          uniqueName: item.uniqueName,
          pluginId: item.pluginId,
          name: item.name,
          tags: item.tags,
          type: item.tags.includes('WATERFLOW') ? 'workflow' : 'tool',
          appId: item.runnables?.APP?.appId || '',
          runnables: item.runnables
        };
        pluginMap.current.push(obj);
      }
    });
    setSkillList([...pluginMap.current]);
  }

  useEffect(() => {
    if (pluginData && pluginData.length) {
      getPluginList();
    }
  }, [pluginData]);
  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-inner'>
            <SkillList skillList={skillList} deleteItem={deleteItem}></SkillList>
          </div>
          <AddSkill
            type='addSkill'
            showModal={showModal}
            setShowModal={setShowModal}
            checkData={skillList}
            confirmCallBack={confirmCallBack}
          />
        </div>
      </div>
    </>
  )
};

export default Skill;
