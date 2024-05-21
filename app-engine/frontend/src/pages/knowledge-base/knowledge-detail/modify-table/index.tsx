import React, { useState, useEffect } from 'react';
import { Button, Modal, Input } from 'antd';
import { Form }from 'antd';
import { updateKnowledgeTable } from '../../../../shared/http/knowledge';

interface props {
  open: boolean;
  data: any;

  setOpen: any;
  refresh: any;
}

// 创建知识库配置
type FieldType = {
  // 知识表名称
  name: ''
};

const ModifyTable = ({ open, data, setOpen, refresh }: props) => {

  const [formLayout, setFormLayout] = useState<any>('vertical');
  const [form] = Form.useForm();

  const initialValues = {
    name: ''
  };
  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 8 }, wrapperCol: { span: 20 } } : null;

  const handleOk = async () => {
    try {
      await form.validateFields();
      const dataFields = form.getFieldsValue();
      await updateKnowledgeTable({
        name: dataFields.name,
        id: data.id,
      });
      refresh();
    } catch (error) {
      
    }
    setOpen(false);
  }

  useEffect(()=> {
    form.setFieldValue('name', data?.name || '')
  }, [data])
  return (<>
    <Modal
        title="修改知识表"
        centered
        open={open}
        onOk={handleOk}
        onCancel={()=> {
          setOpen(false);
        }}
        mousePosition={{ x: 300, y: 300 }}
      >
                   <Form<FieldType>
            {...formItemLayout}
            layout={formLayout}
            form={form}
            initialValues={initialValues}
            style={{ maxWidth: formLayout === 'inline' ? 'none' : 800 }}
          >
            <Form.Item label="知识表名称"  name = 'name' rules={[{ required: true, message: '输入不能为空' }]}>
              <Input placeholder='请输入'/>
            </Form.Item>
          </Form>
      </Modal>
  </>)
};

export {
  ModifyTable,
}