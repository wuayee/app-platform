import React from 'react';
import { PluginIcons } from '../../components/icons/plugin';
export const IconMap = {
  HUGGINGFACE: {
    icon: <PluginIcons.HuggingFaceIcon />,
    name: 'HuggingFace',
  },
  BUILTIN: {
    icon: <PluginIcons.SytemIcon />,
    name: 'Builtin',
  },
  LANGCHAIN: {
    icon: <PluginIcons.LangchainIcon />,
    name: 'Langchain',
  },
  LLAMAINDEX: {
    icon: <PluginIcons.LiamaindexIcon />,
    name: 'Llamaindex',
  },
  FAVOURITE: {
    icon: null,
    name: '我的收藏',
  },
};

export const sourceTabs = [
  { key: 'APP', label: '全部' },
  { key: 'WATERFLOW', label: '流程' },
  { key: 'CODENODESTATE', label: 'code' },
  { key: 'API', label: 'API' },
  // { key: 'BUILTIN', label: 'Builtin' },
  { key: 'HUGGINGFACE', label: 'Huggingface' },
  { key: 'LANGCHAIN', label: 'Langchain' },
  { key: 'LLAMAINDEX', label: 'Llamaindex' },
  { key: 'FAVOURITE', label: '我的收藏' },
];

export const paramsColumns = [
  {
    title: '参数名',
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: '参数类型',
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: '参数说明',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export const outputColumns = [
  {
    title: '参数类型',
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: '参数说明',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export enum PluginCardTypeE {
  MARKET = 'market',
  MY = 'my',
}

export enum PluginStatusTypeE {
  DEPLOYED = 'deployed',
  DEPLOYING = 'deploying',
  UNDEPLOYED = 'undeployed',
  DEPLOYMENT_FAILED = 'deployment_failed'
}
export enum PluginCnType {
  DEPLOYED = '已部署',
  DEPLOYING = '部署中',
  UNDEPLOYED = '未部署',
  DEPLOYMENT_FAILED = '部署失败'
}