
import { Button, Form, Input, Radio, Space, Table, Drawer } from 'antd';
import { RadioChangeEvent } from 'antd/lib';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { createEvalData, modifyDataSetBaseInfo } from '../../../../../shared/http/apps';
import CreateTable from './createTable';
import { CreateType } from './model';


interface props {
  visible: boolean;
  createCallback: Function;
}

interface DataSetInterface {
  ouput: string;
  input: string;
  datasetId: string | null;
}

// 创建参数
interface CreateData {
  data: DataSetInterface[];

  // 作者写死传test
  author: string;

  appId: string;

  datasetName: string;

  description: string;
}

// 创建数据集
type FieldType = {
  datasetName: string;

  description: string;

  data: DataSetInterface[]
};

const CreateSet = ({ visible, createCallback }: props) => {

  const [createOpen, setCreateOpen] = useState(false);
  const [type, setType] = useState(CreateType.MANUAL);


  const [form] = Form.useForm<FieldType>();

  const { tenantId, appId} = useParams();

  useEffect(() => {
    setCreateOpen(visible);
  })


  const changeType = (e: RadioChangeEvent) => {
    setType(e.target.value);
  }

  const closeDrawer = () => {
    form.resetFields();
    createCallback('cancel', {});
  }

  const submit = () => {
    // form.submit();
    // create();
  }

  const onFinish = (value: any) => {
    const data = !showType || type === CreateType.MANUAL ? manualData : uploadData;

    createCallback('submit', { ...value, data });
  }

  // 构建参数
  const buildData = (res: FieldType): CreateData => {

    let data: CreateData = {
      data: res.data.map(item=> ({
        ...item,
        datasetId: item?.datasetId ?? '',
      })),

      // 暂时写死test
      author: 'test',
      appId: appId as string,
      datasetName: res.datasetName,
      description: res.description,
    }
    return data
  }

  // 点击确认按钮
  const clickSubmit = async () => {
    try {
      const res = await form.validateFields();
      await createEvalData(buildData(res));
      
      closeDrawer(); 
    } catch (error) {
      
    }

  }

  return (
    <Drawer
      title={'新建测试集'}
      width={800}
      open={createOpen}
      onClose={closeDrawer}
      maskClosable={false}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={clickSubmit}>确定</Button>
          </Space>
        </div>
      }
    >
      <Form<FieldType> form={form} layout='vertical' onFinish={onFinish}>
        <Form.Item label='新建方式' required>
          <Radio.Group value={type} onChange={changeType}>
            <Space size='large'>
              <Radio value='upload'>上传</Radio>
              <Radio value='manual'>手动</Radio>
            </Space>
          </Radio.Group>
        </Form.Item>
        <Form.Item label='测试集名称' name='datasetName' required rules={[{ required: true, message: '输入不能为空' }]}>
          <Input />
        </Form.Item>
        <Form.Item label='测试集描述' name='description' required rules={[{ required: true, message: '输入不能为空' }]}>
          <Input />
        </Form.Item>
        <Form.Item name='data' required rules={[{ required: true, message: '至少创建一个数据集' }]}>
          <CreateTable type={type}/>
        </Form.Item>
      </Form>
    </Drawer>
  )
}

export default CreateSet;
