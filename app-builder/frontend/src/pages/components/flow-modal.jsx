
import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { Input, Modal, Tooltip, Button, Empty } from 'antd';
import { UndoOutlined, PlusOutlined } from '@ant-design/icons';
import './styles/flow-modal.scss';
import { listData } from './common/mock';

const { Search } = Input;
const FlowModal = (props) => {
  const { modalRef } = props;
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [ searchValue, setSearchValue] = useState('');
  const [ searchList, setSearchList] = useState([]);
  const [ selectedNodes, setSelectedNodes] = useState([]);
  const listRef = useRef(null);
  useEffect(() => {
    setSearchList(listData);
  }, [props])

  const showModal = () => {
    setIsModalOpen(true);
  };
  const handleOk = () => {
    setIsModalOpen(false);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  // 搜索
  const onSearch = (value) => {
    let list = listData.filter(item => item.name.indexOf(value) !== -1);
    setSearchList(list)
  }
  const setSearchListFn = () => {
    setSearchList(listData)
  }
  // 工作流点击
  useEffect(() => {
    listRef.current = selectedNodes;
  }, [selectedNodes])
  function changeSelect(node) {
    if (listRef.current.includes(node.name)) {
      let list = listRef.current.filter(item => item !== node.name);
      setSelectedNodes(list);
    } else {
      setSelectedNodes([...listRef.current, node.name])
    };
  }
  // 刷新
  const refreshList = () => {
    console.log('refresh');
  }

  // 新建工作流
  const addFlow = () => {
    console.log('add');
  }

  return <>{(
    <Modal 
      title='工具流配置'
      width='880px' 
      maskClosable={false}
      centered 
      open={isModalOpen} 
      onOk={handleOk} 
      onCancel={handleCancel}>
      <div className='search-box'>
        <Search 
          placeholder="输入名称搜索" 
          allowClear={false} 
          onSearch={onSearch} 
          onChange={ e => { onSearch(e.target.value) }}/>
          <Button type="primary" shape="circle" size="small" icon={ <UndoOutlined />} onClick={refreshList} className="add-btn"/>
          <Button type="primary" size="small" icon={ <PlusOutlined /> } onClick={addFlow} className="add-btn">
            <span className="add-text">新建</span>
          </Button>
      </div>
      <div className='search-list'>
        {
          searchList.length ? searchList.map((item, index) => {
            return (
              <div 
                className={['node', selectedNodes.includes(item.name) ? 'selected-node' : null ].join(' ')} 
                key={index}
                onClick={() => changeSelect(item)}
                >
                <p className="node-name">{ item.name }</p>
                <Tooltip title={item.description} destroyTooltipOnHide={ true }>
                  <p className="node-description">{ item.description }</p>
                </Tooltip>
              </div>
            )
          }) : <Empty description="暂无数据" />
        }
      </div>
    </Modal>
  )}</>
};

export default FlowModal;
