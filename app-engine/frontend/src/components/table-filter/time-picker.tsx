import { Button, DatePicker } from 'antd';
import React from 'react';
import type { TableColumnType } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { Space } from 'antd';
import { TableIcons } from '../icons/table';

// 自定义搜索面板
const getColumnTimePickerProps = (dataIndex: string, onChange?: any): TableColumnType<string> => ({
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
    <div style={{ padding: 8, display: 'flex', flexDirection: 'column' }} onKeyDown={(e) => e.stopPropagation()}>
      <DatePicker.RangePicker
        placeholder={['请选择', '请选择']}
        value={selectedKeys as any}
        format='YYYY-MM-DD'
        onChange={(date, dateString) => {
          setSelectedKeys((date? date : []) as any)
        }}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => {
            onChange(selectedKeys as string[], confirm, dataIndex);
          }}
          icon={<SearchOutlined />}
          size="small"
          style={{ width: 90 }}
        >
          搜索
        </Button>
        <Button
          onClick={
            () => {
              clearFilters && clearFilters();
              onChange([], confirm, dataIndex);
            }
          }
          size="small"
          style={{ width: 90 }}
        >
          重置
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered: boolean) => (
    <>
      <div style={{ color: filtered ? '#1677ff' : undefined, marginTop: 4 }}>
        <TableIcons.date  />
      </div>
    </>
  ),
});

export {
  getColumnTimePickerProps,
}