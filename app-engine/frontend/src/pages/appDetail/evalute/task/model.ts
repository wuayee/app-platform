import React from 'react';
import { AppIcons } from '../../../../components/icons/app';
export const feedbackType = {
  0: '未反馈',
  1: '点赞',
  2: '点踩',
};

export const traceColumns: TableColumnsType<DataType> = [
  { title: '时间戳', dataIndex: 'time', key: 'time' },
  { title: '节点名', dataIndex: 'nodeId', key: 'nodeId' },
  { title: '耗时（ms）', dataIndex: 'latency', key: 'latency' },
];

export const listColumns = [
  {
    title: '序号',
    dataIndex: 'id',
    key: 'id',
    width: 80,
  },
  {
    title: '输入',
    dataIndex: 'input',
    key: 'input',
    width: 200,
    ellipsis: true,
  },
];

export const compareColumns = [
  {
    title: '输出实际值',
    dataIndex: 'output',
    key: 'output',
  },
  {
    title: '输出期望值',
    dataIndex: 'expectedOutput',
    key: 'expectedOutput',
  },
];

export const inOutColumns = [
  {
    title: '输入',
    dataIndex: 'input',
    key: 'input',
    width: '200px',
  },
  {
    title: '输出',
    dataIndex: 'output',
    key: 'output',
  },
];

export enum TaskStatusE {
  NOT_START = 0,
  IN_PROGRESS = 1,
  FAILURE = 2,
  FINISH = 3,
}

export interface getEvalTaskListParamsI {
  createTimeTo: string;
  author: string;
  createTimeFrom: string;
  appId: string;
  pageSize: number;
  finish: [];
  pageIndex: number;
  version: string;
}

export interface evalTaskI {
  createTime: string;
  author: string;
  passRate: number;
  finish: boolean;
  datasets: Array<string>;
  id: number;
  version: string;
}

export interface traceI {
  output: string;
  input: string;
  latency: number;
  time: string;
  nodeId: string;
}

export interface evalreportI {
  output: string;
  score: number;
  input: string;
  trace: Array<traceI>;
  meta: string;
  latency: number;
  expectedOutput: string;
  id: number;
}
