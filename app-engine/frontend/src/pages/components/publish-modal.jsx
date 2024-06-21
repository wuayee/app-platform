
import React, {  useState, useImperativeHandle } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Input, Modal, Button, Select, Form, Upload } from 'antd';
import { Message } from '@shared/utils/message';
import { httpUrlMap } from '@shared/http/httpConfig';
import { appPublish, updateFlowInfo } from '@shared/http/aipp';
import './styles/publish-modal.scss';
const { TextArea } = Input;

const PublishModal = (props) => {
  const { modalRef, appInfo, publishType, modalInfo, waterFlowName, addId } = props;
  const { appId, tenantId } = useParams();
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [ isPublished, setIsPublished] = useState(false);
  const [ loading, setLoading ] = useState(false);
  const [ form ] = Form.useForm();
  const navigate = useNavigate();
  
  const tagOptions = [
    { value: '编程开发', label: '编程开发' },
    { value: '决策分析', label: '决策分析' },
    { value: '写作助手', label: '写作助手' },
  ];
  const showModal = () => {
    form.setFieldsValue({
      name: appInfo.name,
      description: appInfo.publishedDescription,
      version: appInfo.state === 'active' ? incrementVersion(appInfo.version) : appInfo.version,
      app_type:appInfo.attributes?.app_type
    });
    setIsPublished(appInfo.state === 'active');
    setIsModalOpen(true);
  };
  // 发布点击
  function publishClick() {
    const { PUBLISH_URL } = httpUrlMap[process.env.NODE_ENV];
    appInfo.publishUrl = `${PUBLISH_URL}/aipp/${tenantId}/chat/${appId}`;
    publishType !== 'app' ? updateAppRunningFlow() : publishApp();
  }
  // 发布应用
  async function publishApp() {
    const formParams = await form.validateFields();
    setLoading(true);
    try {
      let params = JSON.parse(JSON.stringify(appInfo));
      params.version = formParams.version;
      params.attributes.app_type = formParams.app_type;
      params.publishedDescription = formParams.description;
      const res = await appPublish(tenantId, appId, params);
      if (res.code === 0) {
        Message({ type: 'success', content: `发布应用成功` });
        setIsModalOpen(false);
        navigate(`/app`);
      }
    } finally {
      setLoading(false)
    }
  }
  // 发布工具流
  async function publishWaterFlow() {
    try {
      const res = await appPublish(tenantId, appId, modalInfo);
      if (res.code === 0) {
        Message({ type: 'success', content: `发布工具流成功` });
        sessionStorage.setItem('uniqueName', res.data.tool_unique_name);
        navigate(-1);
      }
    } finally {
      setLoading(false)
    }
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    if (waterFlowName === '无标题') {
      Message({ type: 'warning', content: '工具流标题不能为空' });
      return
    }
    setLoading(true);
    let params = modalInfo.flowGraph;
    const res = await updateFlowInfo(tenantId, addId, params);
    if (res.code === 0) {
      publishWaterFlow();
    } else {
      setLoading(false)
    }
  }
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  const incrementVersion = (versionString) => {
    const regex = /^(\d+)\.(\d+)\.(\d+)$/;
    const matches = versionString.match(regex);
    const [, major, minor, patch] = matches;
    const newMajor = parseInt(major, 10);
    const newMinor = parseInt(minor, 10);
    const newPatch = parseInt(patch, 10) + 1;
    return `${newMajor}.${newMinor}.${newPatch}`;
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Modal
      title={ publishType === 'app' ? '发布应用' : '发布工具流' }
      width='560px'
      maskClosable={false}
      centered
      open={isModalOpen}
      onOk={publishClick}
      onCancel={handleCancel}
      footer={[
        <Button key="back" onClick={handleCancel}>
          取消
        </Button>,
        <Button key="submit" type="primary" loading={loading} onClick={publishClick}>
          确定
        </Button>
      ]}>
      <div className='search-list'>
        { !isPublished ? 
          (
            <div className="publish-tag">
              <img src='/src/assets/images/ai/info.png' />
              <span>请调试应用，确认无误后发布</span>
            </div>
          ) : 
          (
            <div className="publish-tag">
              <img src='/src/assets/images/ai/info.png' />
              <span>新版本将覆盖历史版本，并不可回退</span>
            </div>
          )
        }
        <Form
          form={form}
          layout="vertical"
          autoComplete="off"
          className='edit-form-content'
        > 
          <Form.Item
            label="分类"
            name="app_type"
            rules={[{ required: true, message: '不能为空' }]}
          >
            <Select options={tagOptions} />
          </Form.Item>
          <Form.Item
            label="版本名称"
            name="version"
            rules={[{ required: true, message: '请输入版本名称' }, { pattern:/^([0-9]+)\.([0-9]+)\.([0-9]+)$/, message: '版本格式错误' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label="版本描述"
            name="description"
          >
            <TextArea rows={3} placeholder="请输入版本描述" />
          </Form.Item>
        </Form>
      </div>
    </Modal>
  )}</>
};

export default PublishModal;
