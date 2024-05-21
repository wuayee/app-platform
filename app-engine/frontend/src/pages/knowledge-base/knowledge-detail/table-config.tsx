import type { TableProps } from 'antd';
import { Button } from 'antd';
import React from 'react';
import { deleteKnowledgeTableType } from '../../../shared/http/knowledge';


export const columnsFunc = (refresh = (type: 'delete' | 'modify', data:any)=> {}): TableProps<any>['columns'] => {
  return [
    {
      title: '名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '条数',
      dataIndex: 'recordNum',
      key: 'recordNum',
    },
    {
      title: '后缀类型',
      dataIndex: 'serviceType',
      key: 'serviceType',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render(value, record, index) {
        const formateTime = (dateStr: Date)=> {
          if(!dateStr) return ''
          const date = new Date(dateStr);
          const y = date.getFullYear();
          const m = date.getMonth() + 1;
          const d = date.getDate();
          const hh = date.getHours();
          const mm = date.getMinutes();
          const ss = date.getSeconds();
          return `${y}-${m}-${d} ${hh}:${mm}:${ss}`;
        }
        return (
        <>
          {formateTime(value)}
        </>)
      },
    },
    {
      title: '操作',
      dataIndex: 'operator',
      key: 'operator',
      width: 200,
      render(_, record, index) {
        const deleteFunc =async () => {
          await deleteKnowledgeTableType(record?.id as string);
          refresh('delete', record);
        };

        const modifyFunc = async ()=> {
          refresh('modify', record);
        }
        return (
        <>
          <div>
            <Button type="link" size="small" onClick={modifyFunc}>修改</Button>
            <Button type="link" size="small" onClick={deleteFunc}>删除</Button>
          </div>
        </>)
      },
    },
  ]
}
