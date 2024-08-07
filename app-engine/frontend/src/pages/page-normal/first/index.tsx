import { Breadcrumb } from 'antd';
import Link from 'antd/es/typography/Link';
import React from 'react';
import { useLocation } from 'react-router-dom';
import HeaderMenus from '../../../components/HeaderMenus';

const First: React.FC = () => {
  const location = useLocation();
  const items = [
    {
      path: '/'+location.pathname.split('/')[1],
      title: '根菜单',
    },
    {
      path: '/demo/first',
      title: '一级菜单',
    },
  ];
  return(
  <div className='aui-fullpage'>
    <div className='aui-header-1'>  
      <div className='aui-title-1'><HeaderMenus items={items} /></div>
    </div>
    <div className='aui-block'>
      {location.pathname}
    <div />
    </div>
  </div>
)};

export default First;
