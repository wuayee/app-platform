
import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Tabs } from "antd";
import { LeftArrowIcon } from '@assets/icon';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import BasicItems from './basic-item';
import ToolItems from './tool-item';

const LeftMenu = (props) => {
  const { dragData, menuClick, setDragData } = props;
  const { tenantId, appId } = useParams();
  const [ activeKey, setActiveKey ] = useState('basic');

  const tabClick = (key) => {
    getAddFlowConfig(tenantId,  {pageNum: 1, pageSize: 100, tag: key}).then(res => {
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
      <div className="tool-modal-tab" style={{ justifyContent: 'flex-start' }}>
        { tab.map(item => {
            return (
              <span className={ activeKey === item.key ? 'active' : null } 
                key={item.key} 
                onClick={() => handleClick(item.key)}
              >
                <span className="text">{ item.name }</span> 
                <span className="line"></span>
              </span>
            )
          })
        }
      </div>
      { activeKey === 'basic' ? <BasicItems dragData={dragData.basic || []} /> : <ToolItems dragData={dragData.tool || []} tabClick={tabClick} /> }
      <div className="arrow-icon" onClick={menuClick}>
        <img src='/src/assets/images/ai/arrow.png'  />
      </div>
    </div>
  )}</>
};


export default LeftMenu;
