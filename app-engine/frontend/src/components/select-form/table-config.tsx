import React, { useEffect, useState } from 'react';
import type { TableProps } from 'antd';
import { Button, Form, Input, InputNumber, Select, Space, Table, Typography } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import './style.scoped.scss';

interface Item {
  key: string;
  colName: string;
  dataType: string;
  vectorService?: string;
  indexType: string;
}

interface EditableCellProps extends React.HTMLAttributes<HTMLElement> {
  editing: boolean;
  dataIndex: string;
  title: any;
  inputType: 'select' | 'input';
  record: Item;
  index: number;
}

const options=[
  { value: 'other', label: '其他索引' },
  { value: 'vector', label: '向量索引' },
];

const dataOptions=[
  { value: 'VARCHAR', label: '字符' },
  { value: 'NUMBER', label: '数字' },
];

const vertorServiceOptions = [
  { value: 'bce-zh', label: 'bce-zh' },
];

// 根据列id获取option
const getOptionsByColId = (id: 'dataType' | 'indexType' | 'vectorService') =>  {
  if(id === 'dataType'){
    return dataOptions;
  }

  if(id === 'indexType'){
    return options;
  }

  if(id === 'vectorService') {
    return vertorServiceOptions;
  }
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
  const form = Form.useFormInstance();
  const indexTypeChange = Form.useWatch('indexType', form);

  useEffect(()=> {
    if(dataIndex === 'indexType') {
      form.setFieldValue('vectorService', '');
    }
  }, [indexTypeChange])
  const inputNode =
    inputType === 'select' ? (
      <Select
      disabled={dataIndex==='vectorService' && indexTypeChange ==='other' ? true: false}
        options={getOptionsByColId(dataIndex as any)}
      />
    ) : (
      <Input />
    );
  const reuired = dataIndex !== 'vectorService' ? [
    {
      required: true,
      message: `Please Input ${title}!`,
    },
  ] : []
  return (
    <td {...restProps}>
      {editing ? (
        <Form.Item
          name={dataIndex}
          style={{ margin: 0 }}
          rules={reuired}
        >
          {inputNode}
        </Form.Item>
      ) : (
        children
      )}
    </td>
  );
};

interface PriceInputProps {
  id?: string;
  value?: Item[];
  onChange?: (value: Item[]) => void;
}

const CustomTable: React.FC<PriceInputProps> = (props) => {
  const { id, value = [], onChange } = props;
  const [form] = Form.useForm();
  const [data, setData] = useState(value);
  const [editingKey, setEditingKey] = useState('');

  const triggerChange = (changedValue: Item[]) => {
    onChange?.([...changedValue]);
  };

  const isEditing = (record: Item) => record.key === editingKey;

  const edit = (record: Partial<Item> & { key: React.Key }) => {
    form.setFieldsValue({ colName: '', dataType: '', indexType: '', ...record });
    setEditingKey(record.key);
  };

  const cancel = () => {
    setEditingKey('');
  };

  const handleAddColumn = () => {
    const key = (data.length + 1).toString();
    setData([{ colName: '', dataType: '', indexType: '', key, vectorService: '' }, ...data]);
    form.setFieldsValue({ colName: '', dataType: '', indexType: '', key, vectorService: '' });
    triggerChange([{ colName: '', dataType: '', indexType: '', key, vectorService: '' }, ...data])
    setEditingKey(key);
  };

    // 移除数据
    const removeData = (record: Partial<Item> & { key: React.Key }) => {
      const index = data.findIndex(item=> record.key === item.key);
      if(index !== -1) {
        const newData = [...data]
        newData.splice(index, 1);
        setData([...newData]);
        triggerChange(newData);
      }
    }

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
      form.setFieldsValue({});
      triggerChange([...newData])
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
      render: (_: any, record: Item) => {
        return (<>
          {dataOptions.find(item=> item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: '索引类型',
      dataIndex: 'indexType',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {options.find(item=> item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: '向量化服务',
      dataIndex: 'vectorService',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {vertorServiceOptions.find(item=> item.value === _)?.label || ''}
        </>)
      }
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
          </Space>
        ) : (
          <Space>
            <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
              编辑
            </Typography.Link>
            <a onClick={()=> {removeData(record)}}>删除</a>
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
      <div className='custom-table-header'>
        <Button type='primary' icon={<PlusOutlined />} onClick={handleAddColumn} disabled={ editingKey ? true : false }>
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
