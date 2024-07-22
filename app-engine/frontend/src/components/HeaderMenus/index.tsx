
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Breadcrumb } from 'antd';
import Link from 'antd/es/typography/Link';

function itemRender(currentRoute, params, items, paths) {
  const navigate=useNavigate();
  const isLast = currentRoute?.path === items[items.length - 1]?.path;
  return isLast ? (
    <span>{currentRoute.title}</span>
  ) : (
    <Link onClick={()=>{
      navigate(currentRoute.path);}}>{currentRoute.title}</Link>
  );
}

const HeaderMenus: React.FC<{items}> = ({items}) => {
  return(
    <Breadcrumb itemRender={itemRender} items={items} />
)};

export default HeaderMenus;
