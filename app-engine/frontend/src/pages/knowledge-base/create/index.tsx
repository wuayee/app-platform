import React, { useState, useEffect, ReactElement } from 'react';
import { Form }from 'antd';
import { Button, Input, Radio } from 'antd';
import { useNavigate } from 'react-router-dom';
type LayoutType = Parameters<typeof Form>[0]['layout'];

const KnowledgeBaseCreate = () => {

  const [form] = Form.useForm();
  const [formLayout, setFormLayout] = useState<LayoutType>('vertical');


  const onFormLayoutChange = ({ layout }: { layout: LayoutType }) => {
    setFormLayout(layout);
  };

  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 4 }, wrapperCol: { span: 14 } } : null;

  const buttonItemLayout =
    formLayout === 'horizontal' ? { wrapperCol: { span: 14, offset: 4 } } : null;
  // 总条数
  const [total, setTotal] = useState(100);

  useEffect(()=> {
    const index = 1;
    setInterval(()=> {
      setTotal(Math.floor(Math.random() * 1000))
    }, 1000)
  }, [])
  return (
    <>
      <div style={{
        width: '100%',
        height: '100%',
        background: '#fff',
        borderRadius: '8px 8px 0px 0px',
        padding: '24px 24px 0 25px',
      }}>
         <Form
          {...formItemLayout}
          layout={formLayout}
          form={form}
          initialValues={{ layout: formLayout }}
          onValuesChange={onFormLayoutChange}
          style={{ maxWidth: formLayout === 'inline' ? 'none' : 600 }}
        >
          <Form.Item label="个人/团队" rules={[{ required: true, message: 'Please input your username!' }]} name = 'any'>
            <Input />
          </Form.Item>
          <Form.Item label="知识库名称" rules={[{ required: true, message: 'Please input your username!' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="知识库描述" rules={[{ required: true, message: 'Please input your username!' }]}>
            <Input />
          </Form.Item>
          <Form.Item {...buttonItemLayout}>
            <Button type="primary">Submit</Button>
          </Form.Item>
        </Form>
      </div>
    </>
  )
}
export default KnowledgeBaseCreate;