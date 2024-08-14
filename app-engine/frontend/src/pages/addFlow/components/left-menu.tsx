
import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import BasicItems from './basic-item';
import ToolItems from './tool-item';

const LeftMenu = (props) => {
  const { dragData, menuClick } = props;
  const [activeKey, setActiveKey] = useState('basic');
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
        {tab.map(item => {
          return (
            <span className={activeKey === item.key ? 'active' : null}
              key={item.key}
              onClick={() => handleClick(item.key)}
            >
              <span className='text'>{item.name}</span>
              <span className='line'></span>
            </span>
          )
        })
        }
      </div>
      {
        activeKey === 'basic' ?
          <BasicItems dragData={dragData.basic || []} /> :
          <ToolItems activeKey={activeKey} />
      }
      <div className='arrow-icon' onClick={menuClick}>
        <img src='./src/assets/images/ai/arrow.png' />
      </div>
    </div>
  )}</>
};


export default LeftMenu;
