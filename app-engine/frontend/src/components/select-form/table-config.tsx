import React, { useState } from 'react';
import type { TableProps } from 'antd';
import { Button, Form, Input, InputNumber, Select, Space, Table, Typography } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import './style.scoped.scss';

interface Item {
  key: string;
  colName: string;
  dataType: string;
  indexType: string;
}

const originData: Item[] = [];
for (let i = 0; i < 100; i++) {
  originData.push({
    key: i.toString(),
    colName: `Edward ${i}`,
    dataType: 'x',
    indexType: ` ${i}`,
  });
}
interface EditableCellProps extends React.HTMLAttributes<HTMLElement> {
  editing: boolean;
  dataIndex: string;
  title: any;
  inputType: 'select' | 'input';
  record: Item;
  index: number;
}

const EditableCell: React.FC<React.PropsWithChildren<EditableCellProps>> = ({
  editing,
  dataIndex,
  title,
  inputType,
  record,
  index,
  children,
  ...restProps
}) => {
  const inputNode =
    inputType === 'select' ? (
      <Select
        options={[
          { value: 'other', label: '其他索引' },
          { value: 'vector', label: '向量索引' },
        ]}
      />
    ) : (
      <Input />
    );

  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{ margin: 0 }}
          rules={[
            {
              required: true,
              message: `Please Input ${title}!`,
            },
          ]}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};

const CustomTable: React.FC = () => {
  const [form] = Form.useForm();
  const [data, setData] = useState(originData);
  const [editingKey, setEditingKey] = useState('');

  const isEditing = (record: Item) => record.key === editingKey;

  const edit = (record: Partial<Item> & { key: React.Key }) => {
    form.setFieldsValue({ colName: '', dataType: '', indexType: '', ...record });
    setEditingKey(record.key);
  };

  const cancel = () => {
    setEditingKey('');
  };

  const save = async (key: React.Key) => {
    try {
      const row = (await form.validateFields()) as Item;

      const newData = [...data];
      const index = newData.findIndex((item) => key === item.key);
      if (index > -1) {
        const item = newData[index];
        newData.splice(index, 1, {
          ...item,
          ...row,
        });
        setData(newData);
        setEditingKey('');
      } else {
        newData.push(row);
        setData(newData);
        setEditingKey('');
      }
    } catch (errInfo) {
      console.log('Validate Failed:', errInfo);
    }
  };

  const columns = [
    {
      title: '列名',
      dataIndex: 'colName',
      editable: true,
    },
    {
      title: '数据类型',
      dataIndex: 'dataType',
      editable: true,
    },
    {
      title: '索引类型',
      dataIndex: 'indexType',
      editable: true,
    },
    {
      title: '向量化服务',
      dataIndex: 'vectorService',
      editable: true,
    },
    {
      title: '描述',
      dataIndex: 'description',
      editable: true,
    },
    {
      title: '操作',
      dataIndex: 'operation',
      width: '10%',
      render: (_: any, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Space>
            <Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
              保存
            </Typography.Link>
            <a onClick={() => setEditingKey('')}>取消</a>
          </Space>
        ) : (
          <Space>
            <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
              编辑
            </Typography.Link>
            <a>删除</a>
          </Space>
        );
      },
    },
  ];

  const mergedColumns: TableProps['columns'] = columns.map((col) => {
    if (!col.editable) {
      return col;
    }
    return {
      ...col,
      onCell: (record: Item) => ({
        record,
        inputType:
          col.dataIndex === 'colName' || col.dataIndex === 'description' ? 'input' : 'select',
        dataIndex: col.dataIndex,
        title: col.title,
        editing: isEditing(record),
      }),
    };
  });

  return (
    <Form form={form} component={false}>
      <Space size={24} className='table-config-item'>
        <div className='table-config-number'>
          <span>表头</span>
          <InputNumber min={1} max={10} defaultValue={3} />
        </div>
        <div className='table-config-number'>
          <span>数据起始位置</span>
          <InputNumber min={1} max={10} defaultValue={3} />
        </div>
        <div className='table-config-number'>
          <span>工资表</span>
          <InputNumber min={1} max={10} defaultValue={3} />
        </div>
      </Space>
      <div className='custom-table-header'>
        <span>表结构</span>
        <Button type='primary' icon={<PlusOutlined />}>
          添加列
        </Button>
      </div>
      <Table
        components={{
          body: {
            cell: EditableCell,
          },
        }}
        bordered
        dataSource={data}
        columns={mergedColumns}
        rowClassName='editable-row'
        pagination={{
          onChange: cancel,
        }}
      />
    </Form>
  );
};

export default CustomTable;
