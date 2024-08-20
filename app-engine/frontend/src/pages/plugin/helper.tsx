import React from 'react';
import { PluginIcons } from '../../components/icons/plugin';
import i18n from '@/locale/i18n';

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
    name: i18n.t('myFavorites'),
  },
};

export const sourceTabs = [
  { key: 'APP', label: i18n.t('all') },
  { key: 'WATERFLOW', label: i18n.t('waterFlow') },
  { key: 'CODENODESTATE', label: 'code' },
  { key: 'API', label: 'API' },
  // { key: 'BUILTIN', label: 'Builtin' },
  { key: 'HUGGINGFACE', label: 'Huggingface' },
  { key: 'LANGCHAIN', label: 'Langchain' },
  { key: 'LLAMAINDEX', label: 'Llamaindex' },
  { key: 'FAVOURITE', label: i18n.t('myFavorites') },
];

export const paramsColumns = [
  {
    title: i18n.t('paramName'),
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export const outputColumns = [
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
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
  DEPLOYED = i18n.t('deployed'),
  DEPLOYING = i18n.t('deployment'),
  UNDEPLOYED = i18n.t('notDeployed'),
  DEPLOYMENT_FAILED = i18n.t('deploymentFailed'),
}