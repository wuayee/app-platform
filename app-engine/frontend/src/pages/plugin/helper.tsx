import React from 'react';
import { PluginIcons } from '../../components/icons/plugin';
export const IconMap = {
  HUGGINGFACE: {
    icon: <PluginIcons.HuggingFaceIcon />,
    name: 'HuggingFace',
  },
  OFFICIAL: {
    icon: null,
    name: '官方',
  },
  LANGCHAIN: {
    icon: null,
    name: 'Langchain',
  },
  LLAMAINDEX: {
    icon: null,
    name: 'Llamaindex',
  },
  FAVOURITE: {
    icon: null,
    name: '我的收藏',
  },
};

export const sourceTabs = [
  { key: 'FIT', label: '全部' },
  { key: 'OFFICIAL', label: '官方' },
  { key: 'HUGGINGFACE', label: 'Huggingface' },
  { key: 'LANGCHAIN', label: 'Langchain' },
  { key: 'LLAMAINDEX', label: 'Llamaindex' },
  { key: 'FAVOURITE', label: '我的收藏' },
];

export const categoryItems = [
  { key: 'FIT', label: '推荐' },
  { key: 'NEWS', label: '新闻阅读' },
  { key: 'UTILITY', label: '实用工具' },
  { key: 'SCIENCE', label: '科教' },
  { key: 'SOCIAL', label: '社交' },
  { key: 'LIFE', label: '便民生活' },
  { key: 'WEBSITE', label: '网站搜索' },
  { key: 'GAMES', label: '游戏娱乐' },
  { key: 'FINANCE', label: '财经商务' },
  { key: 'MEDIA', label: '摄影摄像' },
  { key: 'MEETING', label: '会议记录' },
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
