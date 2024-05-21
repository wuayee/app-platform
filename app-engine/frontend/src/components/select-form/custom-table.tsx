import React, { useState } from 'react';
import type { TableProps } from 'antd';
import { Button, Form, Input, Select, Space, Table, Typography } from 'antd';
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
    dataType: '',
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
      title: '操作',
      dataIndex: 'operation',
      render: (_: any, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
            保存
          </Typography.Link>
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
        inputType: col.dataIndex === 'colName' ? 'input' : 'select',
        dataIndex: col.dataIndex,
        title: col.title,
        editing: isEditing(record),
      }),
    };
  });

  const handleAddColumn = () => {
    const key = data.length.toString();
    setData([{ colName: '', dataType: '', indexType: '', key }, ...data]);
    setEditingKey(key);
  };

  return (
    <Form form={form} component={false}>
      <div className='custom-table-header'>
        <span>自定义知识表</span>
        <Button type='primary' icon={<PlusOutlined />} onClick={handleAddColumn}>
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
