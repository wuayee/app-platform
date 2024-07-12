
import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Spin } from 'antd';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import BasicItems from './basic-item';
import ToolItems from './tool-item';

const LeftMenu = (props) => {
  const { dragData, menuClick, setDragData, loading, setLoading } = props;
  const { tenantId } = useParams();
  const [ activeKey, setActiveKey ] = useState('basic');
  const [ toolKey, setToolKey ] = useState('Builtin');

  const tabClick = (key) => {
    setLoading(true);
    setToolKey(key);
    getAddFlowConfig(tenantId,  {pageNum: 1, pageSize: 1000, tag: key}).then(res => {
      setLoading(false);
      if (res.code === 0) {
        if (key === 'HUGGINGFACE') {
          res.data.tool.forEach(item => {
            item.type = 'huggingFaceNodeState',
            item.context = {
              default_model: item.defaultModel
            }
          })
        };
        setDragData(res.data);
      }
    });
  }
  const tab = [
    { name: '基础', key: 'basic' },
    { name: '插件', key: 'plugin' }
  ]
  const handleClick = (key) => {
    setActiveKey(key);
  }
  return <>{(
    <div className='content-left '>
      <div className='tool-modal-tab' style={{ justifyContent: 'flex-start' }}>
        { tab.map(item => {
            return (
              <span className={ activeKey === item.key ? 'active' : null } 
                key={item.key} 
                onClick={() => handleClick(item.key)}
              >
                <span className='text'>{ item.name }</span> 
                <span className='line'></span>
              </span>
            )
          })
        }
      </div>
      { 
        activeKey === 'basic' ? 
        <Spin spinning={loading}><BasicItems dragData={dragData.basic || []} /> </Spin>: 
        <ToolItems dragData={dragData.tool || []} tabClick={tabClick} loading={loading} toolKey={toolKey}/> 
      }
      <div className='arrow-icon' onClick={menuClick}>
        <img src='/src/assets/images/ai/arrow.png'  />
      </div>
    </div>
  )}</>
};


export default LeftMenu;
