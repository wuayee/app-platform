import React from 'react';
import { AppIcons } from '../../../../components/icons/app';
export const feedbackType = {
  0: '未反馈',
  1: '点赞',
  2: '点踩',
};

export const traceColumns: TableColumnsType<DataType> = [
  { title: '时间戳', dataIndex: 'name', key: 'name' },
  { title: '节点名', dataIndex: 'age', key: 'age' },
  { title: '耗时（ms）', dataIndex: 'address', key: 'address' },
];

export const traceData: DataType[] = [
  {
    name: 'John Brown',
    age: 32,
    address: 'New York No. 1 Lake Park',
    description: 'My name is John Brown, I am 32 years old, living in New York No. 1 Lake Park.',
  },
  {
    name: 'Jim Green',
    age: 42,
    address: 'London No. 1 Lake Park',
    description: 'My name is Jim Green, I am 42 years old, living in London No. 1 Lake Park.',
  },
  {
    name: 'Not Expandable',
    age: 29,
    address: 'Jiangsu No. 1 Lake Park',
    description: 'This not expandable',
  },
  {
    name: 'Joe Black',
    age: 32,
    address: 'Sydney No. 1 Lake Park',
    description: 'My name is Joe Black, I am 32 years old, living in Sydney No. 1 Lake Park.',
  },
];

export const listData = [
  {
    id: 1,
    input: '11111111111111',
  },
  {
    id: 2,
    input: '2222222222222222222222222222222',
  },
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

export const compareData = [
  {
    id: '哈利·波特，是英国女作家J.K.罗琳的魔幻系列小说《哈利·波特》系列及其衍生作品中的主人公，是詹姆·波特和莉莉·波特（原名莉莉·伊万斯）的独生子，出生于1980年7月31日，成年后身高182cm，教父为小天狼星布莱克（Sirius Black），或者说西里斯·布莱克。魔杖长11英寸，冬青木，杖芯是凤凰福克斯的尾羽。',
    input:
      '哈利·波特，是英国女作家J.K.罗琳的魔幻系列小说《哈利·波特》系列及其衍生作品中的主人公，是詹姆·波特和莉莉·波特（原名莉莉·伊万斯）的独生子，出生于1980年7月31日，成年后身高182cm，教父为小天狼星布莱克（Sirius Black），或者说西里斯·布莱克。魔杖长11英寸，冬青木，杖芯是凤凰福克斯的尾羽。',
  },
];

export const compareColumns = [
  {
    title: '输出实际值',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '输出期望值',
    dataIndex: 'input',
    key: 'input',
  },
];

export const inOutColumns = [
  {
    title: '输入',
    dataIndex: 'id',
    key: 'id',
    width: '200px',
  },
  {
    title: '输出',
    dataIndex: 'input',
    key: 'input',
  },
];
