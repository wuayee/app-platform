import { Button, Input, Space, Table } from 'antd';
import React, { useRef } from 'react';
import './style.scoped.scss';
import { FilterDropdownProps } from 'antd/es/table/interface';

const dataSource = [
  {
    id: 1,
    question: '这是一个很长长长长长长长长长长长长长长长长长长长长的问题',
    answer: '回答',
    time: '2024-03-04 14:33:23',
    speed: '20ms',
    user: '用户',
    department: '部门',
    feedback: '1',
  },
];

const FeedBack = () => {
    const searchInput = useRef<InputRef>(null);

  const handleSearch = (
    selectedKeys: string[],
    confirm: FilterDropdownProps['confirm'],
    dataIndex: number
  ) => {
    confirm();
  };

  const handleReset = (clearFilters: () => void) => {
    clearFilters();
  };

  const getColumnSearchProps = (dataIndex: DataIndex): TableColumnType<DataType> => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters, close }) => (
      <div style={{ padding: 8 }} onKeyDown={(e) => e.stopPropagation()}>
        <Input
          ref={searchInput}
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={(e) => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
          style={{ marginBottom: 8, display: 'block' }}
        />
        <Space>
          <Button
            type='primary'
            onClick={() => handleSearch(selectedKeys as string[], confirm, dataIndex)}
            icon={<SearchOutlined />}
            size='small'
            style={{ width: 90 }}
          >
            Search
          </Button>
          <Button
            onClick={() => clearFilters && handleReset(clearFilters)}
            size='small'
            style={{ width: 90 }}
          >
            Reset
          </Button>
          <Button
            type='link'
            size='small'
            onClick={() => {
              close();
            }}
          >
            close
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered: boolean) => (
      <SearchOutlined style={{ color: filtered ? '#1677ff' : undefined }} />
    ),
    onFilter: (value, record) =>
      record[dataIndex]
        .toString()
        .toLowerCase()
        .includes((value as string).toLowerCase()),
    onFilterDropdownOpenChange: (visible) => {
      if (visible) {
        setTimeout(() => searchInput.current?.select(), 100);
      }
    },
  });
    const columns = [
        {
          title: '用户提问',
          dataIndex: 'question',
          key: 'question',
          width:300,
          ellipsis:true,
          ...getColumnSearchProps('question'),
        },
        {
          title: '应用问答',
          dataIndex: 'answer',
          key: 'answer',
          width:300,
          ellipsis:true,
        },
        {
          title: '时间',
          dataIndex: 'time',
          key: 'time',
          width:200,
        },
        {
          title: '相应速度',
          dataIndex: 'speed',
          key: 'speed',
          sorter: (a, b) => a.speed - b.speed,
        },
        {
          title: '用户',
          dataIndex: 'user',
          key: 'user',
        },
        {
          title: '部门',
          dataIndex: 'department',
          key: 'department',
        },
        {
          title: '用户反馈',
          dataIndex: 'feedback',
          key: 'feedback',
        },
        {
          title: '操作',
          dataIndex: 'operate',
          key: 'operate',
        },
      ]; 
return(
<Table dataSource={dataSource} columns={columns} />)};

export default FeedBack;
