
import React, { useEffect, useState, useImperativeHandle } from 'react';
import { useParams } from 'react-router-dom';
import { Input, Modal, Form, Button } from 'antd';
import { updateFlowInfo } from '../../shared/http/aipp';
import { Message } from '../../shared/utils/message';
import './styles/edit-modal.scss';

const EditTitleModal = (props) => {
  const { modalRef, onFlowNameChange, modalInfo } = props;
  const [ form ] = Form.useForm();
  const { appId, tenantId } = useParams();
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const showModal = () => {
    setIsModalOpen(true);
  };
  useEffect(() => {
    form.setFieldsValue({
      name: modalInfo.name,
      description: modalInfo.attributes.description,
    })
  }, [isModalOpen])
  const handleOk = async () => {
    setLoading(true);
    const formParams = await form.validateFields();
    onFlowNameChange(formParams);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
    setLoading(false);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal,
      'handleCancel': handleCancel
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
          <Button key="back" onClick={handleCancel}>
            取消
          </Button>,
          <Button key="submit" type="primary" loading={loading} onClick={handleOk}>
            确定
          </Button>
        ]}>
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
              rules={[{ required: true, message: '请输入名称' }]}
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
      </Modal>
    )}</>
};

export default EditTitleModal;
