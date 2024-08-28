
import React, { useEffect, useState, useImperativeHandle } from 'react';
import { Input, Modal, Button, Select, Form, Upload } from 'antd';
import { useParams } from 'react-router-dom';
import { ToTopOutlined } from '@ant-design/icons';
import { Message } from '@/shared/utils/message';
import { uploadChatFile, updateAppInfo, createAipp } from '@/shared/http/aipp';
import { httpUrlMap } from '@/shared/http/httpConfig';
import { fileValidate } from '@/shared/utils/common';
import knowledgeBase from '@/assets/images/knowledge/knowledge-base.png';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { useTranslation } from 'react-i18next';
import './styles/edit-modal.scss';

const { TextArea } = Input;
const { AIPP_URL } = httpUrlMap[process.env.NODE_ENV];
const EditModal = (props) => {
  const { t } = useTranslation();
  const { modalRef, appInfo, updateAippCallBack, type, addAippCallBack } = props;
  const [form] = Form.useForm();
  const { appId } = useParams();
  const tenantId = TENANT_ID;
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [filePath, setFilePath] = useState('');
  const [fileName, setFileName] = useState('');
  const tagOptions = [
    { value: t('programmingDevelopment'), label: t('programmingDevelopment') },
    { value: t('decisionAnalysis'), label: t('decisionAnalysis') },
    { value: t('writingAssistant'), label: t('writingAssistant') },
  ];
  const showModal = () => {
    form.resetFields(['name']);
    setIsModalOpen(true);
  };
  useEffect(() => {
    form.setFieldsValue({
      name: appInfo.name,
      description: appInfo.attributes?.description,
      greeting: appInfo.attributes?.greeting,
      icon: appInfo.attributes?.icon,
      app_type: appInfo.attributes?.app_type
    })
  }, [isModalOpen])
  const confrimClick = () => {
    if (type === 'add') {
      handleAddOk();
    } else {
      handleOk();
    }
  }
  // 创建应用
  const handleAddOk = async () => {
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      const params = {
        name: formParams.name,
        greeting: formParams.greeting,
        description: formParams.description,
        icon: type === 'add' && filePath ? `${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}` : formParams.icon,
        app_type: t('programmingDevelopment'),
        type: 'app'
      }
      const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc4', params);
      if (res.code === 0) {
        let { id } = res.data;
        handleCancel();
        Message({ type: 'success', content: t('addedSuccessfully') });
        addAippCallBack(id);
      }
    } finally {
      setLoading(false);
    }
  }
  // 编辑应用基本信息
  const handleOk = async () => {
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      formParams.store_id = appInfo.attributes.store_id;
      const params = {
        name: formParams.name,
        attributes: formParams,
        type: appInfo.type,
        version: appInfo.version
      }
      filePath ? params.attributes.icon = `${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}` : params.attributes.icon = appInfo.attributes?.icon;
      const res = await updateAppInfo(tenantId, appId, params);
      if (res.code === 0) {
        updateAippCallBack(res.data);
        handleCancel();
        Message({ type: 'success', content: t('operationSucceeded') });
      }
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  const beforeUpload = (file) => {
    return false
  }
  const onChange = ({ file }) => {
    let validateResult = fileValidate(file);
    if (!validateResult) {
      form.setFieldsValue({
        icon: appInfo.attributes?.icon || ''
      })
    }
    validateResult && pictureUpload(file);
  }
  // 上传图片
  async function pictureUpload(file) {
    let headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      let res = await uploadChatFile(tenantId, appId, formData, headers);
      if (res.code === 0) {
        setFileName(res.data.file_name);
        setFilePath(res.data.file_path);
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') })
    }
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>
    {(
      <Modal
        title={type ? t('addApp') : t('modifyingBasicInfo')}
        width='600px'
        keyboard={false}
        maskClosable={false}
        forceRender={true}
        open={isModalOpen}
        onCancel={handleCancel}
        footer={[
          <Button key='back' onClick={handleCancel}>
            {t('cancel')}
          </Button>,
          <Button key='submit' type='primary' loading={loading} onClick={confrimClick}>
            {t('ok')}
          </Button>
        ]}>
        <div>
          <Form
            form={form}
            layout='vertical'
            autoComplete='off'
            className='edit-form-content'
          >
            <Form.Item
              label={t('name')}
              name='name'
              rules={[{ required: true, message: t('plsEnter') }, {
                type: 'string',
                max: 64,
                message: `${t('characterLength')}：1 - 64`
              }]}
            >
              <Input showCount maxLength={64} />
            </Form.Item>
            <Form.Item
              label={t('description')}
              name='description'
            >
              <TextArea rows={3} showCount maxLength={300} />
            </Form.Item>
            <Form.Item
              label={t('openingRemarks')}
              name='greeting'
            >
              <TextArea rows={3} showCount maxLength={300} />
            </Form.Item>
            {/* <Form.Item
              label={t('classify')}
              name='app_type'
              rules={[{ required: true, message: t('cannotBeEmpty') }]}
            >
              <Select options={tagOptions} />
            </Form.Item> */}
            <Form.Item
              label={t('icon')}
              name='icon'
            >
              <div className='avatar'>
                {filePath ?
                  (<img className='img-send-item' src={`${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`} />)
                  : (<Img icon={appInfo.attributes?.icon} />)}
                <Upload
                  beforeUpload={beforeUpload}
                  onChange={onChange}
                  showUploadList={false}
                  accept='.jpg,.png,.gif,.jpeg'>
                  <Button icon={<ToTopOutlined />}>{t('uploadManually')}</Button>
                </Upload>
              </div>
            </Form.Item>
          </Form>
        </div>
      </Modal>
    )}</>
};

const Img = (props) => {
  const { icon } = props;
  return <>{(
    <span>
      { icon ? <img src={icon} /> : <img src={knowledgeBase} />}
    </span>
  )}</>
}

export default EditModal;
