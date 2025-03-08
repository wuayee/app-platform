/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useState, useImperativeHandle, useRef } from 'react';
import { Input, Modal, Button, Form, Upload, Spin, Radio, Select } from 'antd';
import { useParams } from 'react-router-dom';
import { Message } from '@/shared/utils/message';
import { uploadChatFile, updateAppInfo, createAipp, generatedRequest, createAgent, templateCreateAipp } from '@/shared/http/aipp';
import { httpUrlMap } from '@/shared/http/httpConfig';
import { updateFormInfo } from '@/shared/http/aipp';
import { fileValidate, queryAppCategories } from '@/shared/utils/common';
import knowledgeBase from '@/assets/images/knowledge/knowledge-base.png';
import assistant from '@/assets/images/appdevelop/assistant.png';
import agent from '@/assets/images/appdevelop/agent.png';
import workflow from '@/assets/images/appdevelop/workflow.png';
import type { RadioChangeEvent } from 'antd';
import { TENANT_ID } from '../chatPreview/components/send-editor/common/config';
import { APP_TYPE } from './common/common';
import { useTranslation } from 'react-i18next';
import { findConfigItem } from '@/shared/utils/common';
import { createGraphOperator } from '@fit-elsa/elsa-react';
import { pick, isEmpty } from 'lodash';
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
  const [showImg, setShowImg] = useState(false);
  const [aiLoading, setAiLoading] = useState(false);
  const [appBuiltType, setAppBuiltType] = useState('BASIC');
  const [applicationType, setApplicationType] = useState('assistant');
  const [types, setTypes] = useState([]);
  const inputData = useRef<any>({});
  const graphOperator = useRef<any>({});
  const agentInfo = useRef<any>({})
  const isTemplate = type === 'template';
  const showModal = () => {
    form.resetFields(['name']);
    setIsModalOpen(true);
  };
  useEffect(() => {
    if (isModalOpen && !types.length) {
      getTypes();
    }
    if (isTemplate) {
      setFilePath(appInfo.icon);
      setShowImg(!isEmpty(appInfo.icon));
      form.setFieldsValue({...pick(appInfo, ['name', 'description']), app_type: appInfo.appType});
    } else {
      form.setFieldsValue({
        name: appInfo.name,
        description: appInfo.attributes?.description,
        icon: appInfo.attributes?.icon,
        app_type: appInfo.attributes?.app_type
      });
      if (!type && appInfo.attributes?.icon) {
        setFilePath(appInfo.attributes.icon);
        setShowImg(true);
      } else {
        setFilePath('');
        setShowImg(false);
      }
    }
  }, [isModalOpen]);

  const getTypes = async () => {
    const newTab = await queryAppCategories(tenantId, true);
    setTypes(newTab);
  };

  const confrimClick = () => {
    if (type === 'add') {
      handleAddOk();
    } else if (type === 'template') {
      templateCreate();
    } else {
      handleOk();
    }
  };

  // 根据模板创建应用
  const templateCreate = async () => {
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      const params = {
        id: appInfo.id,
        name: formParams.name,
        tag: formParams.tag,
        description: formParams.description || '',
        icon: filePath || formParams.icon || '',
        app_type: formParams.app_type,
        app_built_type: appInfo.appBuiltType,
        app_category: appInfo.category,
        type: 'app'
      }
      const res = await templateCreateAipp(tenantId, params);
      if (res.code === 0) {
        let { id, aippId } = res.data;
        handleCancel();
        Message({ type: 'success', content: t('addedSuccessfully') });
        addAippCallBack(id, aippId);
      }
    } finally {
      setLoading(false);
    }
  };

  // 获取category
  const getCategory = () => {
    let appCategoryType = '';
    if (applicationType === 'assistant') {
      appCategoryType = 'chatbot';
    } else if (applicationType === 'agent') {
      appCategoryType = 'agent';
    } else if (applicationType === 'workflow') {
      appCategoryType = 'workflow';
    }
    return appCategoryType;
  };

  const getTitle = () => {
    switch (type) {
      case 'add':
        return t('ceateBlankApplication')
      case 'template':
        return t('createAppFromTemplate')
      default:
        return t('modifyingBasicInfo')
    }
  };

  // 创建应用
  const handleAddOk = async () => {
    let agentName = '';
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      if (applicationType === 'agent') {
        const agentParam = {
          description: formParams.description,
        };
        const res: any = await createAgent(agentParam);
        agentName = res.data?.name;
        agentInfo.current = res.data;
      }
      const params = {
        name: applicationType === 'agent' ? agentName : formParams.name,
        description: formParams.description,
        icon: type === 'add' && filePath ? filePath : formParams.icon,
        app_type: formParams.app_type,
        app_built_type: applicationType === 'workflow' ? 'workflow' : APP_TYPE[appBuiltType].name,
        type: 'app',
        app_category: getCategory(),
      }
      const res: any = await createAipp(tenantId, applicationType === 'workflow' ? APP_TYPE.WORK_FLOW.configId : APP_TYPE[appBuiltType].configId, params);
      if (res.code === 0) {
        if (res.data.appCategory === 'agent') {
          updateAgent(res.data);
        } else {
          addSuccessfulCallback(res.data);
        }
      }
    } finally {
      setLoading(false);
    }
  }

  const addSuccessfulCallback = ({ id, aippId }) => {
    handleCancel();
    Message({ type: 'success', content: t('addedSuccessfully') });
    addAippCallBack(id, aippId);
  };

  // 更新智能体信息
  const updateAgent = (detail) => {
    inputData.current = getConfigValue(detail.configFormProperties);
    graphOperator.current = createGraphOperator(JSON.stringify(detail.flowGraph?.appearance));
    const opengingItem = findConfigItem(detail.configFormProperties, 'opening');
    const modelItem = findConfigItem(detail.configFormProperties, 'model');
    const toolsItem = findConfigItem(detail.configFormProperties, 'tools');
    updateConfig(opengingItem, agentInfo.current.greeting);
    updateConfig(modelItem, { systemPrompt: agentInfo.current.prompt });
    updateConfig(toolsItem, agentInfo.current.tools);
    handleConfigDataChange(detail.id, detail.config, { input: Object.values(inputData.current), graph: graphOperator.current?.getGraph() });
  };

  // 更新应用
  const handleConfigDataChange = async (aippId, config, data) => {
    const res = await updateFormInfo(tenantId, aippId, { config, ...data });
    if (res.code === 0) {
      addSuccessfulCallback(res.data);
    }
  }

  // 获取各个配置信息的值
  const getConfigValue = (sourceData, target = {}) => {
    if (!sourceData) return;
    sourceData.forEach(item => {
      target[item.name] = pick(item, ['dataType', 'defaultValue', 'id', 'name', 'nodeId', 'from']);
      if (item.children) {
        target = { ...target, ...getConfigValue(item.children, target) };
      }
    })
    return target;
  };

  // 更新配置信息
  const updateConfig = (config, value) => {
    if (config.from === 'graph') {
      let defaultValue = JSON.parse(JSON.stringify(config.defaultValue));
      // 插件工作流格式单独处理
      if (['tools', 'workflows'].includes(config.name)) {
        defaultValue = config.defaultValue.find(item => item[1] === config.name);
      }
      graphOperator.current.update(defaultValue, value);
    } else {
      inputData.current[config.name].defaultValue = value;
    }
  };
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
        appType: formParams.app_type,
        version: appInfo.version
      }
      filePath ? params.attributes.icon = filePath : params.attributes.icon = appInfo.attributes?.icon;
      const res: any = await updateAppInfo(tenantId, appId, params);
      if (res.code === 0) {
        updateAippCallBack(res.data);
        handleCancel();
        Message({ type: 'success', content: t('editSucceeded') });
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
  // ai生成图片
  const imgLoad = () => {
    setShowImg(true);
  }
  const generatedClick = async () => {
    const name = form.getFieldValue('name');
    const description = form.getFieldValue('description');
    if (!name.trim().length || !description.trim().length) {
      Message({ type: 'warning', content: t('generatedTip') });
      return
    }
    setAiLoading(true);
    const res: any = await generatedRequest({
      name,
      description,
      width: 512,
      height: 512
    }, tenantId)
    if (res.code == 0 && res.data) {
      let data = `data:image/png;base64,${res.data}`
      generatedProcess(data);
    } else {
      setAiLoading(false);
    }
  }
  // base64转
  const generatedProcess = (data) => {
    let binaryData = atob(data.split(',')[1]);
    let arrayBuffer = new ArrayBuffer(binaryData.length);
    let uint8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < binaryData.length; i++) {
      uint8Array[i] = binaryData.charCodeAt(i);
    }
    let blob = new Blob([uint8Array], { type: 'image/png' });
    pictureUpload(blob, 'ai.png')
  }
  // 上传图片
  async function pictureUpload(file, name = '') {
    let headers = {
      'attachment-filename': encodeURI(file.name || ''),
      'Content-Type': 'multipart/form-data',
    };
    try {
      const formData = new FormData();
      formData.append('file', file);
      let res: any = await uploadChatFile(tenantId, appId, formData, headers);
      if (res.code === 0) {
        setFilePath(`${AIPP_URL}/${tenantId}/file?filePath=${res.data.file_path}&fileName=${res.data.file_name}`);
      }
    } catch (err) {
      Message({ type: 'error', content: err.message || t('uploadImageFail') })
    } finally {
      setAiLoading(false);
    }
  }
  // 应用类型切换方法
  const applicationOnChange = (e: RadioChangeEvent) => {
    setApplicationType(e.target.value);
    form.resetFields(['name', 'description']);
  };
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return (
    <>
      <Modal
        title={getTitle()}
        width='526px'
        keyboard={false}
        maskClosable={false}
        forceRender={true}
        open={isModalOpen}
        onCancel={handleCancel}
        footer={[
          <Button key='back' onClick={handleCancel}>
            {t('cancel')}
          </Button>,
          <Button
            className={applicationType === 'agent' ? 'agent-button ' : ''}
            key='submit'
            type='primary'
            loading={loading}
            onClick={confrimClick}
          >
            {applicationType === 'agent' ? (
              <span>
                <img
                  style={{ marginRight: '4px' }}
                  src='/src/assets/images/ai/pic-add.png'
                  alt=''
                />
                {t('intelligentCreate')}
              </span>
            ) : (
              type ? t('create') : t('modify')
            )}
          </Button>,
        ]}
      >
        {type && !isTemplate && (
          <>
            <div style={{ marginBottom: '4px', fontSize: '12px' }}>{t('applicationType')}</div>
            <div className='app-edit-modal'>
              <Radio.Group
                value={applicationType}
                onChange={applicationOnChange}
                className='app-edit-btn-box'
              >
                <Radio.Button value='assistant' className='app-edit-btn app-edit-btn-position'>
                  <img src={assistant} alt='' className='app-edit-btn-img' />
                  {t('conversationAssistant')}
                </Radio.Button>
                <Radio.Button value='agent' className='app-edit-btn app-edit-btn-position'>
                  <img src={agent} alt='' className='app-edit-btn-img' />
                  {t('agent')}
                </Radio.Button>
                <Radio.Button value='workflow' className='app-edit-btn'>
                  <img src={workflow} alt='' className='app-edit-btn-img' />
                  {t('workFlow')}
                </Radio.Button>
              </Radio.Group>
            </div>
          </>
        )}
        {type && applicationType === 'assistant' && !isTemplate && (
          <>
            <div style={{ marginBottom: '4px', marginTop: '16px', fontSize: '12px' }}>
              {t('arrangementTechniques')}
            </div>
            <div className='arrange-model'>
              <div
                className={`arrange-container ${appBuiltType === 'BASIC' ? 'chose' : ''}`}
                onClick={() => setAppBuiltType('BASIC')}
              >
                <div className='model-name'>
                  <img src='./src/assets/images/basic.svg' alt='' />
                  <span>{t('basicArrange')}</span>
                </div>
                <div className='model-desc'>{t('basicArrangeDescription')}</div>
              </div>
              <div
                className={`arrange-container ${appBuiltType === 'WORK_FLOW' ? 'chose' : ''}`}
                onClick={() => setAppBuiltType('WORK_FLOW')}
              >
                <div className='model-name'>
                  <img src='./src/assets/images/workflow.svg' alt='' />
                  <span>{t('workflowArrange')}</span>
                </div>
                <div className='model-desc'>{t('workflowArrangeDescription')}</div>
              </div>
            </div>
          </>
        )}
        <div>
          <Form form={form} layout='vertical' autoComplete='off' className='edit-form-content'>
            {applicationType !== 'agent' && (
              <div>
                <Form.Item label={t('icon')} name='icon'>
                  <div className='avatar'>
                    <Upload
                      beforeUpload={beforeUpload}
                      onChange={onChange}
                      showUploadList={false}
                      accept='.jpg,.png,.gif,.jpeg'
                    >
                      <span className={['upload-img-btn', filePath ? 'upload-img-uploaded' : ''].join(' ')}>
                        {filePath ? (
                          <img
                            className={showImg ? 'img-send-item' : ''}
                            onLoad={imgLoad}
                            src={filePath}
                          />
                        ) : (
                          <img src='/src/assets/images/ai/upload2.png' alt='' />
                        )}
                        {showImg && (
                          <span className='upload-img-mask'>
                            <img src='/src/assets/images/ai/upload3.png' alt='' />
                          </span>
                        )}
                      </span>
                    </Upload>
                    <Spin spinning={aiLoading}>
                      <span className='upload-add-content' onClick={generatedClick}>
                        <img width={'100%'} src='/src/assets/images/ai/pic-add.png' alt='' />
                        <span>{t('AIGenerated')}</span>
                      </span>
                    </Spin>
                  </div>
                </Form.Item>
                <Form.Item
                  label={t('name')}
                  name='name'
                  rules={[
                    { required: true, message: t('plsEnter') },
                    {
                      type: 'string',
                      max: 64,
                      message: `${t('characterLength')}：1 - 64`,
                    },
                  ]}
                >
                  <Input />
                </Form.Item>
              </div>
            )}
            <Form.Item
              label={t('classify')}
              name='app_type'
              rules={[{ required: true, message: t('appCategoryCanNotBeEmpty') }]}
            >
              <Select options={types} fieldNames={{ label: 'label', value: 'key' }} />
            </Form.Item>
            <Form.Item
              label={t('description')}
              rules={[{ required: applicationType === 'agent', message: `${t('plsEnter')}${t('description')}` }]}
              name='description'
            >
              <TextArea rows={3} showCount maxLength={300} />
            </Form.Item>
          </Form>
        </div>
      </Modal>
    </>
  );
};

const Img = (props) => {
  const { icon } = props;
  return <>{(
    <span>
      {icon ? <img src={icon} /> : <img src={knowledgeBase} />}
    </span>
  )}</>
}

export default EditModal;
