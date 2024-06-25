import { Button, Form, Input, Modal, Upload } from 'antd';
import React, { useEffect, useState, useRef } from 'react';
import TextArea from 'antd/es/input/TextArea';
import { UploadFile } from 'antd/lib';
import { ToTopOutlined } from '@ant-design/icons';
import { createAipp, uploadChatFile, uploadImage } from '../../../shared/http/aipp';
import { Message } from '../../../shared/utils/message';
import { useAppSelector } from '../../../store/hook';
import { useNavigate } from 'react-router';

const CreateWorkflow = (props) => {
  const [openWorkFlow, setOpenWorkFlow] = useState(false);
  const { AIPP_URL } =
    process.env.NODE_ENV === 'development'
      ? { AIPP_URL: `${window.location.origin}/api/jober/v1/api` }
      : httpUrlMap[process.env.NODE_ENV];
  const [form] = Form.useForm();
  const [filePath, setFilePath] = useState(undefined);
  const [fileName, setFileName] = useState(undefined);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const navigate = useNavigate();

  // 上传图片
  async function pictureUpload(file) {
    const headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      const res = await uploadImage(tenantId, formData, headers);
      if (res.code === 0) {
        setFileName(res.data.file_name);
        setFilePath(res.data.file_path);
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || '上传图片失败' });
    }
  }

  const beforeUpload = (file) => false;
  const onChange = ({ file }) => {
    pictureUpload(file);
  };
  useEffect(() => {
    if (props.createWorkflowSignal > 0) {
      form.resetFields();
      setOpenWorkFlow(true);
    }
  }, [props?.createWorkflowSignal]);
  return (
    <Modal
      title='创建工作流'
      centered
      open={openWorkFlow}
      onCancel={() => {
        setOpenWorkFlow(false);
      }}
      onOk={async() => {
          await form.validateFields();
          const icon = filePath&&`${AIPP_URL}/api/jober/v1/api/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`
          const res= await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: form.getFieldValue('name'),icon:icon, description: form.getFieldValue('description')});
          const aippId = res.data.id;
          navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
          setOpenWorkFlow(false);
      }}
    >
      <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
        <Form.Item label='头像' name='icon'>
          <div className='avatar'>
            {filePath ? (
              <img
                className='img-send-item'
                src={`${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`}
              />
            ) : (
                <img src='/src/assets/images/knowledge/knowledge-base.png' />
            )}
            <Upload
              beforeUpload={beforeUpload}
              onChange={onChange}
              showUploadList={false}
              accept='.jpg,.png'
            >
              <Button icon={<ToTopOutlined />}>手动上传</Button>
            </Upload>
          </div>
        </Form.Item>
        <Form.Item
          label='名称'
          name='name'
          rules={[
            { required: true, message: '请输入名称' },
            {
              type: 'string',
              max: 64,
              message: '输入字符长度范围：1 - 64',
            },
          ]}
        >
          <Input />
        </Form.Item>
        <Form.Item label='描述' name='description'>
          <TextArea rows={3} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CreateWorkflow;
