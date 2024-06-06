
import React, { useEffect, useState } from 'react';
import { Input } from "antd";
import {
  AddFlowIcon,
  StartIcon,
  DataRetrievalIcon,
  EndIcon,
  ManualCheckIcon,
  LlmIcon,
  IfIcon,
  FitIcon
} from '@assets/icon';
const { Search } = Input;

const BasicItems = (props:any) => {
  const {dragData, tab} = props;
  const config = {
    basic: {
      addFunc: (item, e) => handleClickAddBasicNode(item.type, e),
      dragFunc: (item, e) => handleDragBasicNode(item, e)
    },
    tool: {
      addFunc:(item, e) => handleClickAddToolNode("toolInvokeNodeState", e, item),
      dragFunc:(item, e) => handleDragToolNode(item, e)
    }
  }
  // 根据类型设置图标
  const getIconByType = (type) => {
    return {
      "startNodeStart": <StartIcon />,
      "retrievalNodeState": <DataRetrievalIcon />,
      "llmNodeState": <LlmIcon />,
      "endNodeEnd": <EndIcon />,
      "manualCheckNodeState": <ManualCheckIcon />,
      "fitInvokeNodeState": <FitIcon />,
      "conditionNodeCondition": <IfIcon />,
      "toolInvokeNodeState": <FitIcon />
    }[type];
  }
  // 添加工具
  const handleClickAddBasicNode = (type, e) => {
    e.clientX += 100;
    window.agent.createNode(type, e);
  }
  // 添加插件
  const handleClickAddToolNode = (type, e, metaData) => {
    e.clientX += 100;
    window.agent.createNode(type, e, metaData);
  }
  // 拖动工具添加
  const handleDragBasicNode = (item, e) => {
    e.dataTransfer.setData('itemTab', 'basic');
    e.dataTransfer.setData('itemType', item.type);
  }
  // 拖动插件添加
  const handleDragToolNode = (item, e) => {
    e.dataTransfer.setData('itemTab', 'tool');
    e.dataTransfer.setData('itemType', 'toolInvokeNodeState');
    e.dataTransfer.setData('itemMetaData', JSON.stringify(item));
  }
  // 搜索文本变化，更新工具列表
  const handleSearch = (value, event, source) => {
  }
  return <>
    { tab === 'tool' &&
      <Search
        placeholder="请输入搜索关键词"
        allowClear
        onSearch={handleSearch}
      />
    }
    { dragData.map((item, index) => {
        return (
          <div
            className='drag-item'
            onDragStart={(e) => config[tab].dragFunc(item, e)}
            draggable={true}
            key={index}
          >
            <div className='drag-item-title'>
              <div>
                { tab === 'basic' ? getIconByType(item.type) : getIconByType("toolInvokeNodeState") }
                <span className='content-node-name'>{ item.name }</span>
              </div>
              <span className='drag-item-icon' onClick={(event) => config[tab].addFunc(item, event)}><AddFlowIcon /></span>
            </div>
          </div>
        )
      })
    }
  </>
};


export default BasicItems;
