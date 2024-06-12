import type { MenuProps } from 'antd';
import { ReactElement } from 'react';
import { Icons } from '../components/icons/index';
import KnowledgeBase from '../pages/knowledge-base';
import KnowledgeBaseCreate from '../pages/knowledge-base/create';
import KnowledgeBaseDetail from '../pages/knowledge-base/knowledge-detail';
import Plugin from '../pages/plugin';
import Demo from '../pages/demo';
import ChatHome from '../pages/chatEngineHome/index.jsx';
import ChatRunning from '../pages/chatRunning/index';
import AppDetail from '../pages/appDetail';
import AippIndex from '../pages/aippIndex';
import AddFlow from '../pages/addFlow';
import FlowDetail from '../pages/detailFlow';
import ChatShare from '../pages/chatShare';
import Apps from '../pages/apps';
import KnowledgeBaseDetailCreateTable from '../pages/knowledge-base/knowledge-detail/create-table';
import KnowledgeBaseDetailImportData from '../pages/knowledge-base/knowledge-detail/import-data';
import Model from '../pages/model';
import ModelBase from '../pages/model-base';
import ModelTuning from '../pages/model-tuning';
import ModelDetail from '../pages/model/model-detail';
import AppDev from '../pages/appDev/index';
import IndustryTerminology from '../pages/knowledge-base/knowledge-detail/industry-terminology';

export type MenuItem = Required<MenuProps>['items'][number] & {
  component?: (() => ReactElement) | React.FC<any>;
  children?: MenuItem[] | null;
  label: string;
  key: string;
  hidden?: boolean;
  title?: string;
};

// key为页面链接不允许相同, 需要子数组就增加children数组, 设置hidden则不显示在菜单上
export const routeList: MenuItem[] = [
  {
    key: '/home',
    icon: Icons.home({}),
    label: '首页',
    component: ChatHome,
    children: [
      {
        key: '/:tenantId/chatShare/:appId/:shareId',
        icon: Icons.app({}),
        label: '分享对话',
        component: ChatShare,
        hidden: true,
      },
    ],
  },
  {
    key: '/app',
    icon: Icons.app({}),
    label: '应用市场',
    component: Apps,
    children: [],
  },
  {
    key: '/app-develop',
    icon: Icons.app({}),
    label: '应用开发',
    component: AppDev,
    children: [
      {
        key: "/app-develop/:tenantId/app-detail/:appId",
        icon: Icons.app({}),
        label: 'app编排',
        component: AippIndex,
        hidden: true,
      },
      {
        key: "/app-develop/:tenantId/app-detail/add-flow/:appId",
        icon: Icons.app({}),
        label: '新增工具流',
        component: AddFlow,
        hidden: true,
      },
      {
        key: "/app-develop/:tenantId/app-detail/flow-detail/:appId",
        icon: Icons.app({}),
        label: '工具流',
        component: FlowDetail,
        hidden: true,
      },
      {
        key: '/app-develop/:tenantId/appDetail/:appId',
        icon: Icons.app({}),
        label: '',
        component: AppDetail,
        hidden: true,
      },
      {
        key: "/app-develop/:tenantId/chat/:appId",
        icon: Icons.app({}),
        label: '',
        component: ChatRunning,
        hidden: true,
      },
    ],
  },
  {
    key: '/robot-market',
    icon: Icons.app({}),
    label: '机器人市场',
    component: Demo,
    hidden: true,
  },
  {
    key: '/model',
    icon: Icons.app({}),
    label: '模型服务',
    component: Model,
    children: [
      {
        key: 'model/detail',
        icon: Icons.app({}),
        label: 'app编排',
        component: ModelDetail,
        hidden: true,
      },
    ],
  },
  {
    key: '/model-base',
    icon: Icons.app({}),
    label: '模型仓管理',
    component: ModelBase,
  },
  {
    key: '/model-tuning',
    icon: Icons.app({}),
    label: '模型精调',
    component: ModelTuning,
    hidden: true
  },
  {
    key: '/knowledge-base',
    icon: Icons.app({}),
    label: '知识库',
    title: '知识库概览',
    component: KnowledgeBase,
    children: [
      {
        key: '/knowledge-base/create',
        icon: Icons.app({}),
        label: '创建知识库',
        component: KnowledgeBaseCreate,
        hidden: true,
      },
      {
        key: '/knowledge-base/knowledge-detail',
        icon: Icons.app({}),
        label: '知识库详情',
        component: KnowledgeBaseDetail,
        hidden: true,
        children: [
          {
            key: '/knowledge-base/knowledge-detail/create-table',
            icon: Icons.app({}),
            label: '添加知识表',
            component: KnowledgeBaseDetailCreateTable,
            hidden: true,
          },
          {
            key: '/knowledge-base/knowledge-detail/import-data',
            icon: Icons.app({}),
            label: '导入数据',
            component: KnowledgeBaseDetailImportData,
            hidden: true,
          },
          {
            key: '/knowledge-base/knowledge-detail/industry-terminology',
            icon: Icons.app({}),
            label: '详情',
            component: IndustryTerminology,
            hidden: true,
          },
        ],
      },
    ],
  },
  {
    key: '/plugin',
    icon: Icons.app({}),
    label: '插件',
    component: Plugin,
  },
  {
    key: '/Tooling',
    icon: Icons.app({}),
    label: '工具',
    component: Demo,
    hidden: true,
  },
  {
    key: '/WorkStream',
    icon: Icons.app({}),
    label: '工作流',
    component: Demo,
    hidden: true,
  },
  {
    key: '/group',
    icon: Icons.app({}),
    label: '团队',
    component: Demo,
    hidden: true,
  },
];

// 生成菜单
export const getMenus = (routeList: MenuItem[]): MenuItem[] => {
  const menus: MenuItem[] = routeList.map((route) => {
    let children: MenuItem[] = [];
    if (route.children && route.children.length) {
      children = getMenus(route.children);
    }
    if (children.length) {
      return {
        ...route,
        children,
      };
    } else {
      return {
        ...route,
        children: null,
      };
    }
  });
  return menus.filter((item) => !item?.hidden);
};

// 将路由展平
export const flattenRoute = (routeList: MenuItem[]): MenuItem[] => {
  let flattenRouteList: MenuItem[] = [];
  const rootLayer = routeList.map((item) => {
    if (item?.children && item.children.length) {
      flattenRouteList = [...flattenRouteList, ...flattenRoute(item.children)];
    }
    return item;
  });
  return [...rootLayer, ...flattenRouteList];
};

// 根据key值返回路由, 传入展平的数组
export const getRouteByKey = (routeList: MenuItem[], key: string): MenuItem | null => {
  return routeList.find((item) => item.key === key) || null;
};
