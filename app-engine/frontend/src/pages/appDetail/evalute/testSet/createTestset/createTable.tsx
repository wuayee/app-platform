import React, { useState } from 'react';
import { Button, Form, Space, Table } from 'antd';
import { CallbackMethod, CreateType } from './model';
import CreateItem from './createItem';
import LiveUpload from '../../../../../components/upload';
import { downTemplateUrl, uploadTestSetFile } from '../../../../../shared/http/apps';

interface DataSetInterface {
  ouput: string;
  input: string;
  datasetId: string;
}

interface PriceInputProps {
  id?: string;
  value?: DataSetInterface[];
  onChange?: (value: DataSetInterface[]) => void;
}

const CreateTable: React.FC<PriceInputProps & { type : CreateType}> = (props) => {
  const { id, value = [], onChange, type } = props;


  const triggerChange = (changedValue: DataSetInterface[]) => {
    onChange?.([...changedValue]);
  };

  const defaultItemData = {
    input: '',
    output: '',
    datasetId: '',
    index: -1,
  }

  const [params, setParams] = useState(defaultItemData);
  const [itemOpen, setItemOpen] = useState(false);

  const editItemDrawer = (data: any, index: number) => {
    if (data) {
      const tempParams = {
        input: data?.input,
        output: data?.output,
        datasetId: data?.datasetId,
        index,
      }
      setParams(tempParams);
    }
    setItemOpen(true);
  };

  const showItemDrawer = () => {
    const tempParams = {
      index: -1,
      input: '',
      output: '',
      datasetId: '',
    }
    setParams(tempParams);
    setItemOpen(true);
  };

  const callback = (operate: string, data: any, index: number) => {
    switch (operate) {
      case CallbackMethod.SUBMIT:
        const updateData = [...value];
        if (index >= 0) {
          updateData[index] = data;
        } else {
          updateData.push(data);
        }
        triggerChange(updateData);
    }
    setItemOpen(false);
  }

  const columns = [
    {
      key: 'input',
      dataIndex: 'input',
      title: '输入',
      ellipsis: true
    },
    {
      key: 'output',
      dataIndex: 'output',
      title: '输出',
      ellipsis: true
    },
    {
      key: 'action',
      title: '操作',
      render(_: any, record: any, index: any) {
        const edit = () => {
          editItemDrawer(record, index);
        }
        const delItem = () => {
          const updateData = [...value];
          updateData.splice(index, 1);
          triggerChange(updateData);
        }
        return (
          <Space size='middle'>
            <a onClick={edit}>编辑</a>
            <a onClick={delItem}>删除</a>
          </Space>
        )
      },
      hidden: type !== CreateType.MANUAL,
      width: 120
    },
  ];

  // 上传文件
  const customRequest = async (file: any) => {
      const formData = new FormData();
      formData.append('file', file)
      const res: any[] = await uploadTestSetFile(formData);

      const dataList: any[] = res.map((data, index)=> ({
        input: data?.input ?? '',
        output: data?.output ?? '',
        datasetId: '',
      }))

      triggerChange(dataList);
      return res;
    }
  
  return (
    <>
      {(type === CreateType.UPLOAD) ?
        <>
          <Form.Item label='上传' required>
            <LiveUpload customRequest={customRequest}/>
          </Form.Item>
          <div style={{
            fontSize: 12,
            lineHeight: '18px',
            color: '#808080'
          }}>最大文件大小 5MB，<a href={downTemplateUrl} download="eval_dataset_template.xlsx" style={{
            color: '#2673E5'
          }}>下载模板</a></div>
        </>
        
        
        :
        <Button
          type='primary'
          style={{ minWidth: '96px', margin: '8px 0 16px' }}
          onClick={showItemDrawer}
        >创建</Button>
      }
      <Table
        dataSource={value}
        columns={columns}
        pagination={{
          simple: true,
          size: 'small'
        }}
      />
      <CreateItem params={params} visible={itemOpen} callback={callback} />
    </>
  );
};

export default CreateTable;
