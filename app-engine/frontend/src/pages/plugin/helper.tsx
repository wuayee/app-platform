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
  { key: 'ALL', label: '全部' },
  { key: 'BUILTIN', label: 'Builtin' },
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
    width: 300,
  },
  {
    title: '参数类型',
    dataIndex: 'type',
    key: 'type',
    width: 300,
  },
  {
    title: '参数说明',
    dataIndex: 'description',
    key: 'description',
    ellipsis: true,
  },
];

export enum PluginCardTypeE{
  MARKET='market',
  MY='my',
}
