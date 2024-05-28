
import { Button, Form, Input, Drawer, Space } from 'antd';
import React, { useEffect, useState } from 'react';

const { TextArea } = Input;

interface props {
  params: any;
  visible: boolean;
  callback: any;
}

interface DataSetInterface {
  ouput: string;
  input: string;
  datasetId: string;
}

const CreateItem = ({ params, visible, callback }: props) => {

  const [form] = Form.useForm<DataSetInterface>();
  const [open, setOpen] = useState(false);

  useEffect(() => {
    setOpen(visible);
    form.setFieldValue('input', params.input);
    form.setFieldValue('output', params.output);
    form.setFieldValue('datasetId', params.datasetId ?? '');
  });


  const closeDrawer = () => {
    callback('cancel', {});
  }

  const onFinish = (value: any) => {
    callback('submit', value, params.index);
  }

  const submit = () => {
    form.submit();
  }

  return (
    <Drawer
      title="手动新建测试集"
      width={800}
      open={open}
      maskClosable={false}
      onClose={closeDrawer}
      destroyOnClose={true}
      footer={
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Space>
            <Button style={{ minWidth: 96 }} onClick={closeDrawer}>取消</Button>
            <Button type='primary' style={{ minWidth: 96 }} onClick={submit}>确定</Button>
          </Space>
        </div>
      }
    >
      <Form<DataSetInterface>
        form={form}
        layout='vertical'
        onFinish={onFinish}
      >
        <Form.Item label='输入' required name='input' rules={[{ required: true, message: '输入不能为空' }]}>
          <TextArea rows={4} />
        </Form.Item>
        <Form.Item label='输出' required name='output' rules={[{ required: true, message: '输入不能为空' }]}>
          <TextArea rows={4} />
        </Form.Item>
      </Form>
    </Drawer>

  )
}

export default CreateItem;
