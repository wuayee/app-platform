
import React, { useEffect, useState } from 'react';
import { Drawer, Form, Input, Button } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import { createAipp } from "@shared/http/aipp";
import { CloseOutlined } from '@ant-design/icons';

const AddWaterFlow = (props) => {
  const { open, setOpen } = props;
  const [ loading, setLoading ] = useState(false);
  const [ form ] = Form.useForm();
  const { tenantId, appId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    form.setFieldsValue({
      name: '',
      description: ''
    })
  }, [open]);
  const confrimClick = async () => {
    setLoading(true);
    try {
      const formParams = await form.validateFields();
      const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: formParams.name });
      if (res.code === 0) {
        const aippId = res.data.id;
        navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
      }
    } finally {
      setLoading(false);
    }
  }
  return <>
    <Drawer
      title='创建工具流'
      placement='right'
      width='420px'
      closeIcon={false}
      onClose={() => setOpen(false)}
      open={open}
      footer={[
        <Button key="back" onClick={() => setOpen(false)}>
          取消
        </Button>,
        <Button key="submit" type="primary" loading={loading} onClick={confrimClick}>
          确定
        </Button>
      ]}
      extra={
        <CloseOutlined onClick={() => setOpen(false)}/>
      }>
      <div className='edit-form-list'>
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
        className='edit-form-content'
        >
        <Form.Item
            label="名称"
            name="name"
            rules={[{ required: true, message: '请输入名称' }, {
              type: 'string',
              max: 64,
              message: '输入字符长度范围：1 - 64'
            }]}
          >
          <Input />
        </Form.Item>
        <Form.Item
          label="简介"
          name="description"
        >
          <Input />
        </Form.Item>
      </Form>
      </div>
    </Drawer>
  </>
};


export default AddWaterFlow;
