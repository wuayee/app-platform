import type { TableProps } from 'antd';
import { Button } from 'antd';
import React from 'react';
import { KnowledgeIcons } from '../../../components/icons';
import { deleteKnowledgeTableType } from '../../../shared/http/knowledge';
import i18n from '@/locale/i18n';

export const columnsFunc = (refresh = (type: 'delete' | 'modify' | 'clickHeader', data: any) => { }): TableProps<any>['columns'] => {
  return [
    {
      title: i18n.t('name'),
      dataIndex: 'name',
      key: 'name',
      render(value, record) {
        const onClick = () => {
          refresh('clickHeader', record)
        }
        return (<>
          <div style={{
            display: 'flex',
            gap: 8,
            cursor: 'pointer'
          }} onClick={onClick}>
            {record?.format === 'TABLE' ? (<KnowledgeIcons.tableXlsx />) : (<KnowledgeIcons.tableText />)}
            <span style={{
              color: '#2673E5',
              fontSize: 14
            }}>{value}</span>
          </div>
        </>)
      }
    },
    {
      title: i18n.t('numberOfPieces'),
      dataIndex: 'recordNum',
      key: 'recordNum',
    },
    {
      title: i18n.t('backendService'),
      dataIndex: 'serviceType',
      key: 'serviceType',
    },
    {
      title: i18n.t('createdAt'),
      dataIndex: 'createdAt',
      key: 'createdAt',
      render(value, record, index) {
        const formateTime = (dateStr: Date) => {
          if (!dateStr) return ''
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
      title: i18n.t('operate'),
      dataIndex: 'operator',
      key: 'operator',
      width: 200,
      render(_, record, index) {
        const deleteFunc = async () => {
          await deleteKnowledgeTableType(record?.id as string);
          refresh('delete', record);
        };

        const modifyFunc = async () => {
          refresh('modify', record);
        }
        return (
          <>
            <div>
              <Button type="link" size="small" onClick={modifyFunc}>{i18n.t('modify')}</Button>
              <Button type="link" size="small" onClick={deleteFunc}>{i18n.t('delete')}</Button>
            </div>
          </>)
      },
    },
  ]
}
