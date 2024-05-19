import React, { useState, useEffect } from 'react';
import type { MenuProps } from 'antd';
import { Breadcrumb, Layout, Menu, theme, ConfigProvider,  } from 'antd';
import { HashRouter, Route, useNavigate, Routes, useLocation } from 'react-router-dom';
import { routeList, flattenRoute, getRouteByKey, getMenus, MenuItem } from '../../router/route'


const flattenRouteList = flattenRoute(routeList);


const AppLayout: React.FC = () => {
  const [breadcrumb, setBreadcrumb] = useState<MenuItem[]>([])

  const navigate = useNavigate();
  const location = useLocation();

  useEffect(()=> {
    const { pathname } = location;
    const pathGroup = pathname.split('/').filter(item=> item);

    const breadcrumbKeyList = pathGroup.map((item, index)=> '/'+pathGroup.slice(0, index+ 1).join('/'));

    const routeItem = breadcrumbKeyList.map(item => getRouteByKey(flattenRouteList, item)) as MenuItem[]

    setBreadcrumb(routeItem);
  }, [location])
  

  return (
    <Breadcrumb style={{ margin: '16px 0' }}>
    {breadcrumb.map((route)=> (<>
      <Breadcrumb.Item key={route.key}>{route.title || route.label}</Breadcrumb.Item>
    </>))}
  </Breadcrumb>
  );
};

export default AppLayout;