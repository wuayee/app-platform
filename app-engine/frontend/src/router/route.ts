import type { MenuProps } from 'antd';
import { ReactElement } from 'react';
import { Icons } from '../components/icons/index';
import KnowledgeBase from '../pages/knowledge-base';
 
type MenuItem = Required<MenuProps>['items'][number] & { component?: () => ReactElement, children?: MenuItem[], label: string, key: string};
 
// key为页面链接不允许相同, 需要子数组就增加children数组
export const routeList: MenuItem[] = [
  {
    key: '/home',
    icon: Icons.home({}),
    label: '首页',
  },
  {
    key: '/robot-market',
    icon: Icons.app({}),
    label: '机器人市场',
  },
  {
    key: '/plugin-market',
    icon: Icons.app({}),
    label: '插件市场',
  },
  {
    key: '/app',
    icon: Icons.app({}),
    label: '应用',
  },
  {
    key: '/mode',
    icon: Icons.app({}),
    label: '模型',
  },
  {
    key: '/knowledge-base',
    icon: Icons.app({}),
    label: '知识库',
    component: KnowledgeBase,
  },
  {
    key: '/plugin',
    icon: Icons.app({}),
    label: '插件',
  },
  {
    key: '/group',
    icon: Icons.app({}),
    label: '团队',
  },
];
 
 
// 将路由展平
export const flattenRoute = (routeList: MenuItem[]): MenuItem[] => {
  let flattenRouteList: MenuItem[] = [];
  const rootLayer = routeList.map(item=> {
    if(item?.children && item.children.length) {
      flattenRouteList = [...flattenRouteList, ...flattenRoute(item.children)]
    }
    return (item);
  })
  return [...rootLayer, ...flattenRouteList]
}
 
// 根据key值返回路由, 传入展平的数组
export const getRouteByKey = (routeList: MenuItem[], key: string): MenuItem | null => {
  return routeList.find(item=> item.key === key) || null;
}