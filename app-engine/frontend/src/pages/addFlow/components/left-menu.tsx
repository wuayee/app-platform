
import React, { useEffect, useState } from 'react';
import { Tabs } from "antd";
import { LeftArrowIcon } from '@assets/icon';
import BasicItems from './basic-item';

const LeftMenu = (props) => {
  const { type, dragData, menuClick } = props;

  const items = [
    {
      key: 'basic',
      label: '基础',
      children: <BasicItems dragData={dragData.basic || []} tab={"basic"}/>,
      icon: <LeftArrowIcon onClick={menuClick} />
    },
    {
      key: 'tool',
      label: '工具',
      children: <BasicItems dragData={dragData.tool || []} tab={"tool"}/>,
    },
  ];
  return <>{(
    <div className='content-left'>
        <Tabs defaultActiveKey="basic" items={items} />
    </div>
  )}</>
};


export default LeftMenu;
