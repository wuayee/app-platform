
import React, { useEffect, useState } from 'react';
import { Tooltip, Dropdown, Space } from "antd";
import { LinkIcon, AtIcon, HistoryIcon, ArrowDownIcon, LanguagesIcon } from '@/assets/icon';
import robot from "@assets/images/ai/robot1.png";

// 操作按钮
const EditorBtnHome = (props) => {
  const { aippInfo, setOpen, onClear } = props;
  const [ items, setItems ] = useState([
    { key: '1', label: '自动' },
    { key: '2', label: '中文' },
    { key: '3', label: '英文' },
    { key: '4', label: '其他语言' },
  ]);
  const [ currentName, setCurrentName ] = useState('自动');
  const onClick = ({ key }) => {
    let clickItem = items.filter(item => item.key === key)[0];
    setCurrentName(clickItem.label);
  };
  return <>{(
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={aippInfo.attributes?.icon ? aippInfo.attributes?.icon : robot} alt="" />
          <div className="switch-app">
            <span className="item-name" title={aippInfo.name || '应用'}>{aippInfo.name || '应用'}</span>
            <ArrowDownIcon className="arrow-icon" />
          </div>
          <LinkIcon />
          <AtIcon />
        </div>
      </div>
      <div className="inner-right">
        <div className="inner-item">
          <LanguagesIcon />
          <Dropdown 
            menu={{ items, onClick  }} 
            trigger="click" 
            placement="top" 
            overlayStyle={{ maxHeight: '200px', overflow: 'auto' }}>
            <Space>
              <span className="languages-span">{ currentName }</span>
              <ArrowDownIcon className="arrow-icon" />
            </Space>
          </Dropdown>
          <HistoryIcon  onClick={() => setOpen(true)}/>
          <span className="item-clear" onClick={onClear}>+ 新聊天</span>
        </div>
      </div>
    </div>
  )}</>
}


export default EditorBtnHome;
