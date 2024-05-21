
import { Form, Input, Radio, Space, Table } from 'antd';
import React, { useState } from 'react';
import LiveUpload from '../../../../components/upload';
import './style.scoped.scss';

const CreateSet: React.FC = () => {

  const [form] = Form.useForm();

  const dataSource = Array.from({ length: 30 }).fill(null).map((_, index) => ({
    input: `输入${index}`,
    output: `输出${index}`,
  }));

  const columns = [
    {
      key: 'input',
      dataIndex: 'input',
      title: '输入'
    },
    {
      key: 'output',
      dataIndex: 'output',
      title: '输出',

    },
  ];


  return (
    <Form form={form} layout='vertical'>
      <Form.Item label='新建方式' required>
        <Radio.Group>
          <Space size='large'>
            <Radio value='upload'>上传</Radio>
            <Radio value='manual'>手动</Radio>
          </Space>
        </Radio.Group>
      </Form.Item>
      <Form.Item label='测试集名称' required>
        <Input />
      </Form.Item>
      <Form.Item label='测试集描述' required>
        <Input />
      </Form.Item>
      <Form.Item label='上传' required>
        <LiveUpload />
      </Form.Item>
      <Table
        dataSource={dataSource}
        columns={columns}
        pagination={{
          simple: true,
          size: 'small'
        }}
      />
    </Form>
  )
}

export default CreateSet;
