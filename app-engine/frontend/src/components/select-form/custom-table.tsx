import React, { useState } from 'react';
import type { TableProps } from 'antd';
import { Button, Form, Input, Select, Space, Table, Typography } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import './style.scoped.scss';
import { useTranslation } from "react-i18next";
import i18n from '../../locale/i18n';

interface Item {
  key: string;
  colName: string;
  dataType: string;
  indexType: string;
}

const options=[
  { value: 'other', label: i18n.t('otherIndex') },
  { value: 'vector', label: i18n.t('vectorIndex') },
];

const dataOptions=[
  { value: 'VARCHAR', label: i18n.t('character') },
  { value: 'NUMBER', label: i18n.t('number') },
];


interface EditableCellProps extends React.HTMLAttributes<HTMLElement> {
  editing: boolean;
  dataIndex: string;
  title: any;
  inputType: 'select' | 'input';
  record: Item;
  index: number;
}

// 选择数据源表单配置
type FieldType = {
  // 选择文档类型
  datasourceType: 'local' | 'nas' | 'custom';

  // 上传文本文件 local类型
  selectedFile?: FileList;

  // NAS 类型
  nasUrl?: string;

  // nas文件路径
  nasFileUrl?: string;

  // 文本自定义内容 custom
  textCustom?: string;

  // 表格自定义内容 custom
  tableCustom?: any[];
};

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
  const { t } = useTranslation();
  const inputNode =
    inputType === 'select' ? (
      <Select
        options={dataIndex === 'dataType' ? dataOptions: options}
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
              message: `${t('plsEnter')} ${title}!`,
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

interface PriceInputProps {
  id?: string;
  value?: Item[];
  onChange?: (value: Item[]) => void;
}

const CustomTable: React.FC<PriceInputProps> = (props) => {
  const { t } = useTranslation();
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
      form.setFieldsValue({});
      triggerChange([...newData])
    } catch (errInfo) {
      console.log('Validate Failed:', errInfo);
    }
  };

  const columns = [
    {
      title: t('colName'),
      dataIndex: 'colName',
      width: '30%',
      editable: true,
    },
    {
      title: t('dataType'),
      dataIndex: 'dataType',
      width: '30%',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {dataOptions.find(item=> item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: t('indexType'),
      dataIndex: 'indexType',
      width: '30%',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {options.find(item=> item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: t('operate'),
      dataIndex: 'operation',
      width: '10%',
      render: (_: any, record: Item) => {
        const editable = isEditing(record);
        return editable ? (
          <Space>
            <Typography.Link onClick={() => save(record.key)} style={{ marginRight: 8 }}>
              {t('save')}
            </Typography.Link>
          </Space>
        ) : (
          <Space>
            <Typography.Link disabled={editingKey !== ''} onClick={() => edit(record)}>
              {t('edit')}
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
        inputType: col.dataIndex === 'colName' ? 'input' : 'select',
        dataIndex: col.dataIndex,
        title: col.title,
        editing: isEditing(record),
      }),
    };
  });

  const handleAddColumn = () => {
    const key = (data.length + 1).toString();
    setData([{ colName: '', dataType: '', indexType: '', key }, ...data]);
    form.setFieldsValue({ colName: '', dataType: '', indexType: '', key });
    triggerChange([{ colName: '', dataType: '', indexType: '', key }, ...data])
    setEditingKey(key);
  };

  return (
    <Form<FieldType> form={form} component={false}>
      <div className='custom-table-header'>
        <Button type='primary' icon={<PlusOutlined />} onClick={handleAddColumn} disabled={ editingKey ? true : false }>
          {t('addCol')}
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
          disabled: editingKey ? true : false,
        }}
      />
    </Form>
  );
};

export default CustomTable;
