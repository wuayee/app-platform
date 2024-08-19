
import React, { useState } from 'react';
import BasicItems from './basic-item';
import ToolItems from './tool-item';
import { useTranslation } from "react-i18next";

const LeftMenu = (props) => {
  const { t } = useTranslation();
  const { dragData, menuClick } = props;
  const [activeKey, setActiveKey] = useState('basic');
  const tab = [
    { name: t('basic'), key: 'basic' },
    { name: t('plugin'), key: 'plugin' }
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
