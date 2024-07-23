
import React, {  useState, useImperativeHandle } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Input, Modal, Button, Select, Form } from 'antd';
import TextEditor from './text-editor';
import { Message } from '@shared/utils/message';
import { httpUrlMap } from '@shared/http/httpConfig';
import { appPublish, updateFlowInfo } from '@shared/http/aipp';
import { versionStringCompare } from '@shared/utils/common';
import './styles/publish-modal.scss';
const { TextArea } = Input;

const PublishModal = (props) => {
  const { modalRef, appInfo, publishType } = props;
  const { appId, tenantId } = useParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading ] = useState(false);
  const [text, setText] = useState('');
  const [form] = Form.useForm();
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
      version: appInfo.version,
      app_type: publishType !== 'app' ? 'waterflow' : appInfo.attributes?.app_type
    });
    setText('');
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
    if (versionStringCompare(formParams.version, appInfo.version) === -1) {
      Message({ type: 'warning', content: `当前版本为${appInfo.version} 发布版本不能低于当前版本` });
      return
    }
    setLoading(true);
    try {
      let params = JSON.parse(JSON.stringify(appInfo));
      params.version = formParams.version;
      params.attributes.app_type = formParams.app_type;
      params.publishedDescription = formParams.description;
      params.publishedUpdateLog = text;
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
    const formParams = await form.validateFields();
    if (versionStringCompare(formParams.version, appInfo.version) === -1) {
      Message({ type: 'warning', content: `当前版本为${appInfo.version} 发布版本不能低于当前版本` });
      setLoading(false);
      return
    }
    appInfo.version = formParams.version;
    appInfo.publishedDescription = formParams.description;
    appInfo.publishedUpdateLog = text;
    try {
      const res = await appPublish(tenantId, appId, appInfo);
      if (res.code === 0) {
        Message({ type: 'success', content: `发布工具流成功` });
        sessionStorage.setItem('uniqueName', res.data.tool_unique_name);
        const appEngineId = sessionStorage.getItem('appId');
        appEngineId && navigate(`/app-develop/${tenantId}/app-detail/${appEngineId}`);
      }
    } finally {
      setLoading(false)
    }
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    setLoading(true);
    let params = appInfo.flowGraph;
    const res = await updateFlowInfo(tenantId, appId, params);
    if (res.code === 0) {
      publishWaterFlow();
    } else {
      setLoading(false)
    }
  }
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>{(
    <Modal
      title={ publishType === 'app' ? '发布应用' : '发布工具流' }
      width={700}
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
        { appInfo.attributes?.latest_version ? 
          (
            <div className="publish-tag">
              <img src='/src/assets/images/ai/info.png' />
              <span>新版本将覆盖历史版本，并不可回退</span>
            </div>
          ) :
          (
            <div className="publish-tag" style={{ display: publishType === 'app' ? 'block' : 'none'}}>
              <img src='/src/assets/images/ai/info.png' />
              <span>请调试应用，确认无误后发布</span>
            </div>
          )
        }
        <Form
          form={form}
          layout="vertical"
          autoComplete="off"
          className='edit-form-content'
        > {
            publishType === 'app' && 
            <Form.Item
              label="分类"
              name="app_type"
              rules={[{ required: true, message: '不能为空' }]}
            >
              <Select options={tagOptions} />
            </Form.Item>
          }
          
          <Form.Item
            label="版本名称"
            name="version"
            rules={[
              { required: true, message: '请输入版本名称' }, 
              { pattern:/^([0-9]+)\.([0-9]+)\.([0-9]+)$/, message: '版本格式错误' }
            ]}
          >
            <Input showCount maxLength={10} />
          </Form.Item>
          <Form.Item
            label="版本描述"
            name="description"
          >
            <TextArea rows={4} placeholder="请输入版本描述" showCount maxLength={300} />
          </Form.Item>
          {
            <Form.Item
              label="版本公告"
              name="updateLog"
            >
              <TextEditor text={text} setText={setText} />
            </Form.Item>
          }
        </Form>
      </div>
    </Modal>
  )}</>
};

export default PublishModal;
