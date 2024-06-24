
import React, { useEffect, useState, useRef } from 'react';
import { Input, Pagination, Empty, Spin } from "antd";
import { AddFlowIcon } from '@assets/icon';
import { handleClickAddToolNode, handleDragToolNode } from '../utils';
import ToolModal from './tool-modal';
import '../styles/tool-item.scss';

const { Search } = Input;

const ToolItem = (props) => {
  const { dragData, tabClick, loading, toolKey } = props;
  const [ name, setName ] = useState('');
  const [ pageNum, setPageNum ] = useState(1);
  const [ list, setList ] = useState([]);
  const [ showModal, setShowModal ] = useState(false);
  const listRef = useRef([]);
  useEffect(() => {
    console.log(dragData);
    
    listRef.current = JSON.parse(JSON.stringify(dragData));
    setList(listRef.current);
  }, [dragData])
  const tab = [
    { name: '官方', key: 'Builtin' },
    { name: 'HuggingFace', key: 'HUGGINGFACE' },
    { name: 'LangChain', key: 'LANGCHAIN' },
  ]
  // 搜索文本变化，更新工具列表
  const handleSearch = (value, event, source) => {
    setPageNum(1);
    if (!value.trim().length) {
      setList(listRef.current);
    } else {
      let arr = listRef.current.filter(item => item.name.indexOf(value.trim()) !== -1);
      console.log(arr);
      
      setList(arr);
    }
  }
  const handleClick = (key) => {
    setPageNum(1);
    tabClick(key);
    setName('');
  }
  // 分页
  const selectPage = (curPage: number, curPageSize: number) => {
    if (pageNum !== curPage) {
      setPageNum(curPage);
    }
  }
  return <>
    <Search
      placeholder="请输入搜索关键词"
      allowClear
      value={name}
      onChange={ e => { setName(e.target.value) }}
      onSearch={handleSearch}
    />
    <div className="tool-tab">
      { tab.map(item => {
          return (
            <span className={ toolKey === item.key ? 'active' : null } 
              key={item.key} 
              onClick={() => handleClick(item.key)}
            >{ item.name }
            </span>
          )
        })
      }
      <span className="more" onClick={() => setShowModal(true)}>更多</span>
    </div>
    <Spin spinning={loading}>
      {
        list.length > 0 && <div className="drag-list">
          { list.map((item, index) => {
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
                        <img src='/src/assets/images/ai/plugin.png' alt='' />
                        { item.name }
                      </span>
                    </div>
                    <span className='drag-item-icon' 
                      onClick={(event) => handleClickAddToolNode(item.type || 'toolInvokeNodeState', event, item)}>
                      <img src='/src/assets/images/ai/flow.png'  />
                    </span>
                  </div>
                </div>
              )
            })
          }
        </div>
      }
      { list.length === 0 && <div className="tool-empty"><Empty description="暂无数据" /></div> }
    </Spin>
    <ToolModal showModal={showModal} setShowModal={setShowModal} />
  </>
};
export default ToolItem;
