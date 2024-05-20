import type { MenuProps } from "antd";
import { ReactElement } from "react";
import { Icons } from "../components/icons/index";
import KnowledgeBase from "../pages/knowledge-base";
import KnowledgeBaseCreate from "../pages/knowledge-base/create";
import KnowledgeBaseDetail from '../pages/knowledge-base/knowledge-detail';
import Demo from "../pages/demo";
import AppDetail from "../pages/appDetail";

export type MenuItem = Required<MenuProps>['items'][number] & 
  { 
    component?: (() => ReactElement) | React.FC<any>, 
    children?: MenuItem[] | null, 
    label: string, 
    key: string, 
    hidden?: boolean, 
    title?: string
  };

// key为页面链接不允许相同, 需要子数组就增加children数组, 设置hidden则不显示在菜单上
export const routeList: MenuItem[] = [
    {
        key: "/home",
        icon: Icons.home({}),
        label: "首页",
        component: Demo,
    },
    {
        key: "/robot-market",
        icon: Icons.app({}),
        label: "机器人市场",
        component: Demo,
    },
    {
        key: "/plugin-market",
        icon: Icons.app({}),
        label: "插件市场",
    },
    {
        key: "/app",
        icon: Icons.app({}),
        label: "应用",
        component: Demo,
    },
    {
        key: "/mode",
        icon: Icons.app({}),
        label: "模型",
        component: Demo,
    },
    {
        key: "/knowledge-base",
        icon: Icons.app({}),
        label: "知识库",
        title: '知识库概览',
        component: KnowledgeBase,
        children: [
            {
                key: "/knowledge-base/create",
                icon: Icons.app({}),
                label: "创建知识库",
                component: KnowledgeBaseCreate,
                hidden: true,
            },
            {
              key: '/knowledge-base/knowledge-detail',
              icon: Icons.app({}),
              label: '小魔方知识库',
              component: KnowledgeBaseDetail,
              hidden: true,
            },
        ],
    },
    {
        key: "/plugin",
        icon: Icons.app({}),
        label: "插件",
        component: Demo,
    },
    {
        key: "/group",
        icon: Icons.app({}),
        label: "团队",
        component: Demo,
    },
    {
        key: "/app-detail",
        component: AppDetail,
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
            flattenRouteList = [
                ...flattenRouteList,
                ...flattenRoute(item.children),
            ];
        }
        return item;
    });
    return [...rootLayer, ...flattenRouteList];
};

// 根据key值返回路由, 传入展平的数组
export const getRouteByKey = (
    routeList: MenuItem[],
    key: string
): MenuItem | null => {
    return routeList.find((item) => item.key === key) || null;
};
