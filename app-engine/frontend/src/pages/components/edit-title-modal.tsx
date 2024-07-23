
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import { Input, Modal, Form, Button } from 'antd';
import { FlowContext } from '../aippIndex/context';
import './styles/edit-modal.scss';

const EditTitleModal = (props) => {
  const { modalRef, onFlowNameChange } = props;
  const [ form ] = Form.useForm();
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [ loading, setLoading ] = useState(false);
  const { appInfo } = useContext(FlowContext);
  const showModal = () => {
    setIsModalOpen(true);
  };
  useEffect(() => {
    form.setFieldsValue({
      name: appInfo.name,
      description: appInfo.attributes?.description,
    })
  }, [isModalOpen])
  const handleOk = async () => {
    const formParams = await form.validateFields();
    setLoading(true);
    onFlowNameChange(formParams);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
    setLoading(false);
  };
  const handleLoading = () => {
    setLoading(false);
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal,
      'handleCancel': handleCancel,
      'handleLoading': handleLoading
    }
  })
  return <>
    {(
      <Modal
        title='修改基础信息'
        width='600px'
        maskClosable={false}
        forceRender={true}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={[
          <Button key='back' onClick={handleCancel}>
            取消
          </Button>,
          <Button key='submit' type='primary' loading={loading} onClick={handleOk}>
            确定
          </Button>
        ]}>
        <div className='edit-form-list' style={{ marginBottom: '30px' }}>
          <Form
            form={form}
            layout='vertical'
            autoComplete='off'
            className='edit-form-content'
          >
            <Form.Item
              label='名称'
              name='name'
              rules={[{ required: true, message: '请输入名称' },  {
                type: 'string',
                max: 64,
                message: '输入字符长度范围：1 - 64'
              }]}
            >
              <Input maxLength={64} showCount />
            </Form.Item>
            <Form.Item
              label='简介'
              name='description'
            >
              <Input.TextArea rows={4} maxLength={300} showCount />
            </Form.Item>
          </Form>
        </div>
      </Modal>
    )}</>
};

export default EditTitleModal;
