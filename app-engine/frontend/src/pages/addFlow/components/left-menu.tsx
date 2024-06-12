
import React, { useContext } from 'react';
import { Tabs } from "antd";
import { LeftArrowIcon } from '@assets/icon';
import BasicItems from './basic-item';
import ToolItems from './tool-item';

const LeftMenu = (props) => {
  const { dragData, menuClick } = props;
  const items = [
    {
      key: 'basic',
      label: '基础',
      children: <BasicItems dragData={dragData.basic || []} />,
      icon: <LeftArrowIcon onClick={menuClick} />
    },
    {
      key: 'tool',
      label: '插件',
      children: <ToolItems dragData={dragData.tool || []} />,
    },
  ];
  return <>{(
    <div className='content-left'>
      <Tabs defaultActiveKey="basic" items={items} />
    </div>
  )}</>
};


export default LeftMenu;
