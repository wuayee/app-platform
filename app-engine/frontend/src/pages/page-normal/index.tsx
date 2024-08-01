import { Breadcrumb } from 'antd';
import Link from 'antd/es/typography/Link';
import React from 'react';
import { useLocation,useNavigate } from 'react-router-dom';
import HeaderMenus from '../../components/HeaderMenus';

const Demo: React.FC = () => {
  const location = useLocation();
  const items = [
    {
      path: location.pathname.split('/')[1],
      title: '根菜单',
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

export default Demo;
