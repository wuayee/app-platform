
import React, { useEffect, useState } from 'react';
import { Input } from "antd";
import { AddFlowIcon } from '@assets/icon';
import { handleClickAddToolNode, handleDragToolNode } from '../utils';
import ToolModal from './tool-modal';
import '../styles/tool-item.scss';

const { Search } = Input;

const ToolItem = (props) => {
  const { dragData } = props;
  const [ showModal, setShowModal ] = useState(false);
  const [ activeKey, setActiveKey ] = useState('1');
  const tab = [
    { name: '官方', key: '1' },
    { name: 'HuggingFace', key: '2' },
    { name: 'LangChain', key: '3' },
  ]
  // 搜索文本变化，更新工具列表
  const handleSearch = (value, event, source) => {}
  const handleClick = (key) => {
    setActiveKey(key);
  }
  return <>
    <Search
      placeholder="请输入搜索关键词"
      allowClear
      onSearch={handleSearch}
    />
    <div className="tool-tab">
      { tab.map(item => {
          return (
            <span className={ activeKey === item.key ? 'active' : null } 
              key={item.key} 
              onClick={() => handleClick(item.key)}
            >{ item.name }
            </span>
          )
        })
      }
      <span className="more" onClick={() => setShowModal(true)}>更多</span>
    </div>
    { dragData.map((item, index) => {
        return (
          <div
            className='drag-item'
            onDragStart={(e) => handleDragToolNode(item, e)}
            draggable={true}
            key={index}
          >
            <div className='drag-item-title'>
              <div>
                <span className='content-node-name node-tool'>
                  <img src='/src/assets/images/ai/tool.png' alt='' />
                  { item.name }
                </span>
              </div>
              <span className='drag-item-icon' 
                onClick={(event) => handleClickAddToolNode(item.type || 'toolInvokeNodeState', event, item)}>
                  <AddFlowIcon />
              </span>
            </div>
          </div>
        )
      })
    }
    <ToolModal showModal={showModal} setShowModal={setShowModal} />
  </>
};


export default ToolItem;
