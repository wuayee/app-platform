import { Button, Input } from 'antd';
import React from 'react';
import type { TableColumnType } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import { Space } from 'antd';


// 自定义搜索面板
const getColumnSearchProps = (dataIndex: string, onChange?: any): TableColumnType<string> => ({
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
    <div style={{ padding: 8 }} onKeyDown={(e) => e.stopPropagation()}>
      <Input
        placeholder={`请输入`}
        value={selectedKeys[0]}
        onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
        onPressEnter={() => {
          onChange(selectedKeys as string[], confirm, dataIndex)
        }}
        style={{ marginBottom: 8, display: 'block' }}
      />
      <Space>
        <Button
          type="primary"
          onClick={() => {
            onChange(selectedKeys as string[], confirm, dataIndex)
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
    <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
  ),
});

export {
  getColumnSearchProps,
}