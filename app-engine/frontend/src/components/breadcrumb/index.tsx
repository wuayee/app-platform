import React, { useState, useEffect } from 'react';
import { Breadcrumb } from 'antd';
import { useLocation, useNavigate } from 'react-router-dom';
import { routeList, flattenRoute, getRouteByKey, MenuItem } from '../../router/route';



const BreadcrumbSelf = ({currentLabel}: {currentLabel?: string}) => {
  const flattenRouteList = flattenRoute(routeList);
  const [breadcrumb, setBreadcrumb] = useState<MenuItem[]>([])

  const location = useLocation();
  const navgate = useNavigate();

  useEffect(()=> {
    const { pathname } = location;
    const pathGroup = pathname.split('/').filter(item=> item);

    const breadcrumbKeyList = pathGroup.map((item, index)=> '/'+pathGroup.slice(0, index+ 1).join('/'));

    const routeItem = breadcrumbKeyList.map(item => getRouteByKey(flattenRouteList, item)) as MenuItem[]

    setBreadcrumb(routeItem);
  }, [location])

  const changeNavgate = (route: string) => {
    navgate(route)
  }
  

  return (
    <Breadcrumb style={{ margin: '16px 0' }}>
    {breadcrumb.map((route, index)=> (<>
      <Breadcrumb.Item key={route.key} href={'#'+route.key}>{index === breadcrumb.length -1 ? currentLabel || route.title || route.label : route.title || route.label}</Breadcrumb.Item>
    </>))}
  </Breadcrumb>
  );
};

export default BreadcrumbSelf;