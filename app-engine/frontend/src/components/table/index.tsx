import React, { useRef } from 'react';
import { SearchOutlined } from '@ant-design/icons';
import type { InputRef, TableColumnsType, TableColumnType } from 'antd';
import { Button, Input, Pagination, Space, Table } from 'antd';
import type { FilterDropdownProps } from 'antd/es/table/interface';

const handleSearch = (
  selectedKeys: string[],
  confirm: FilterDropdownProps['confirm'],
  dataIndexName: DataIndex
) => {
  confirm();
  console.log(dataIndexName, selectedKeys);
};

const handleReset = (clearFilters: () => void) => {
  clearFilters();
};

export const getColumnSearchProps = (
  dataIndexName: DataIndex,
  searchInput
): TableColumnType<DataType> => ({
  filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
    <div style={{ padding: 8, width: 250 }} onKeyDown={(e) => e.stopPropagation()}>
      <Input
        ref={searchInput}
        value={selectedKeys[0]}
        onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
        onPressEnter={() => handleSearch(selectedKeys as string[], confirm, dataIndexName)}
        style={{ marginBottom: 8, display: 'block' }}
      />
      <Space style={{ float: 'right', marginBottom: 8 }}>
        <Button
          type='default'
          style={{ width: 90, height: 30 }}
          onClick={() => {
            close();
          }}
        >
          关闭
        </Button>
        <Button
          type='primary'
          onClick={() => handleSearch(selectedKeys as string[], confirm, dataIndexName)}
          size='small'
          style={{ width: 90, height: 30, backgroundColor: '#2673e5' }}
        >
          确定
        </Button>
      </Space>
    </div>
  ),
  filterIcon: (filtered: boolean) => (
    <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
  ),
  onFilter: (value, record) =>
    record[dataIndexName]
      .toString()
      .toLowerCase()
      .includes((value as string).toLowerCase()),
  onFilterDropdownOpenChange: (visible) => {
    if (visible) {
      setTimeout(() => searchInput.current?.select(), 100);
    }
  },
});

const LiveUiTable: React.FC<{ dataSource; columns; onChange }> = ({
  dataSource,
  columns,
  onChange,
}) => (
  <div>
    <Table
      dataSource={dataSource}
      columns={columns}
      onChange={onChange}
      pagination={{
        position: ['bottomRight'],
        size: 'small',
        showQuickJumper: true,
        defaultCurrent: 1,
        showSizeChanger: true,
        showTotal: (total) => <div style={{ position: 'absolute', left: 0 }}>共{total}条</div>,
        onChange: (pageNo, pageSize) => {
          console.log('rwqe');
        },
      }}
    />
  </div>
);

export default LiveUiTable;
