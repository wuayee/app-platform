import React, { useEffect, useState } from 'react';
import type { TableProps } from 'antd';
import { Button, Form, Input, Select, Space, Table, Typography } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { useTranslation } from 'react-i18next';
import { getKnowledgeTableType } from '@/shared/http/knowledge';
import './style.scoped.scss';

interface Item {
  key: string;
  colName: string;
  dataType: string;
  description?: string;
  indexType: string;
}

interface EditableCellProps extends React.HTMLAttributes<HTMLElement> {
  editing: boolean;
  dataIndex: string;
  title: any;
  inputType: 'select' | 'input';
  record: Item;
  index: number;
  options?: { value: string, label: string }[];
}

const EditableCell: React.FC<React.PropsWithChildren<EditableCellProps>> = ({
  editing,
  dataIndex,
  title,
  inputType,
  record,
  index,
  children,
  options,
  ...restProps
}) => {
  const form = Form.useFormInstance();
  const indexTypeChange = Form.useWatch('indexType', form);
  const inputNode =
    inputType === 'select' ? (
      <Select
        disabled={false}
        options={options}
      />
    ) : (
        <Input />
      );
  const reuired = dataIndex === 'description' ? [] : [
    {
      required: true,
      message: `Please Input ${title}!`,
    },
  ]
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
  const { t } = useTranslation();
  const options = [
    { value: 'NORMAL', label: t('commonIndex') },
    { value: 'VECTOR', label: t('vectorIndex') },
    { value: 'NONE', label: t('none') },
  ];
  const dataOptions = [
    { value: 'VARCHAR', label: t('character') },
    { value: 'NUMBER', label: t('number') },
  ];

  const getOptionsByColId = (id: 'dataType' | 'indexType' | string) => {
    if (id === 'dataType') {
      return dataOptions;
    }
    if (id === 'indexType') {
      return options;
    }
    return [];
  }

  // 根据列id获取option
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
    setData([{ colName: '', dataType: '', indexType: '', key }, ...data]);
    form.setFieldsValue({ colName: '', dataType: '', indexType: '', key });
    triggerChange([{ colName: '', dataType: '', indexType: '', key }, ...data])
    setEditingKey(key);
  };

  // 移除数据
  const removeData = (record: Partial<Item> & { key: React.Key }) => {
    const index = data.findIndex(item => record.key === item.key);
    if (index !== -1) {
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
      title: t('colName'),
      dataIndex: 'colName',
      editable: true,
    },
    {
      title: t('dataType'),
      dataIndex: 'dataType',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {dataOptions.find(item => item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: t('indexType'),
      dataIndex: 'indexType',
      editable: true,
      render: (_: any, record: Item) => {
        return (<>
          {options.find(item => item.value === _)?.label || ''}
        </>)
      }
    },
    {
      title: t('description'),
      dataIndex: 'description',
      editable: true,
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
              <a onClick={() => { removeData(record) }}>{t('delete')}</a>
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
        options: getOptionsByColId(col.dataIndex),
      }),
    };
  });

  // 获取向量化服务类型
  const getTableType = async () => {
    try {
      let typeList = await getKnowledgeTableType();
      if (typeList && typeList.length) {
        setServiceOptions(typeList.filter(item => item.type === 'EMBEDDING').map(type => ({
          value: type.id,
          label: type.name
        })))

      }
    }
    catch (error) {

    }
  }

  useEffect(() => {
    getTableType()
  }, []);

  return (
    <Form form={form} component={false}>
      <div className='custom-table-header'>
        <Button type='primary' icon={<PlusOutlined />} onClick={handleAddColumn} disabled={editingKey ? true : false}>
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
        }}
      />
    </Form>
  );
};

export default CustomTable;
