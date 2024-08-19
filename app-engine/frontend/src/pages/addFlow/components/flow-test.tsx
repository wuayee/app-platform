
import React, { useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Drawer, Form, Alert, Spin } from 'antd';
import { StartIcon, CloseIcon, RunIcon } from '@assets/icon';
import { Message } from '@shared/utils/message';
import { reTestInstance } from '@shared/http/aipp';
import { messageProcess } from '../../chatPreview/utils/chat-process';
import { workflowDebug, getTestVersion } from '@shared/http/sse';
import { useAppSelector } from '@/store/hook';
import { EventSourceParserStream } from '@shared/event-source/stream';
import RenderFormItem from './render-form-item';
import RuntimeForm from '../../chatPreview/components/receive-box/runtime-form';
import { useTranslation } from "react-i18next";

const Index = (props) => {
  const {
    debugTypes,
    setTestTime,
    setTestStatus,
    setShowDebug,
    showDebug,
    elsaRunningCtl,
    setShowFlowChangeWarning
  } = props;
  const { t } = useTranslation();
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [formConfig, setFormConfig] = useState({});
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const { tenantId, appId } = useParams();
  const [form] = Form.useForm();
  const timerRef = useRef(null);
  const runningInstanceId = useRef('');
  // 关闭测试抽屉
  const handleCloseDebug = () => {
    setLoading(false);
    setShowDebug(false);
  }
  const handleRunTest = () => {
    setShowFlowChangeWarning(false);
    elsaRunningCtl.current?.reset();
    setTestStatus(null);
    setTestTime(0);
    form.validateFields().then((values) => {
      runningStart(values);
    }).catch((errorInfo) => {
      Message({ type: 'warning', content: t('plsEnterRequiredItem') });
    });
  }
  // 请求参数拼接
  const runningStart = (values) => {
    let chatParams: any = {
      'app_id': appId,
      'question': values.Question,
      'context': {
        'use_memory': false,
        'user_context': { ...values },
        'dimension': values.dimension
      }
    };
    if (chatId) {
      chatParams['chat_id'] = chatId;
    };
    handleRun(chatParams);
  };
  // 点击运行
  const handleRun = async (params) => {
    setLoading(true);
    const res = await workflowDebug(tenantId, params);
    if (res.status !== 200) {
      Message({ type: 'error', content: t('startDebugFail') });
      setLoading(false);
      return;
    }
    const reader = res?.body?.pipeThrough(new TextDecoderStream())
      .pipeThrough(new EventSourceParserStream()).getReader();
    testStreaming(reader);
  }
  // 流式输出sse数据
  const testStreaming = async (reader) => {
    let getReady = false;
    while (true) {
      const sseResData = await reader?.read();
      const { done, value } = sseResData;
      if (!done) {
        try {
          let msgStr = value.data;
          const receiveData = JSON.parse(msgStr);
          if (receiveData.status === 'READY' && !getReady) {
            getReady = true;
            runningInstanceId.current = receiveData.instance_id;
            const versionRes = await getTestVersion(tenantId, appId);
            const { aipp_id, version } = versionRes.data;
            if (aipp_id && version) {
              startDebugMission(aipp_id, version, receiveData.instance_id);
            } else {
              setLoading(false);
              elsaRunningCtl.current && elsaRunningCtl.current.reset();
              Message({ type: 'error', content: t('startRunFail') });
              break;
            }
          } else {
            sseTestProcess(receiveData);
          }
        } catch (e) {
          console.info(e);
        }
      } else {
        break;
      };
    };
  }
  // 接收表单消息
  const sseTestProcess = (messageData) => {
    try {
      // 普通日志
      messageData.answer?.forEach(log => {
        if (log.type === 'FORM') {
          let obj = messageProcess(runningInstanceId.current, log.content);
          setFormConfig(obj.formConfig);
          setOpen(true);
        }
      });
    } catch (err) {
      console.info(err);
    }
  }
  // 开始调试
  const startDebugMission = (aippId, version, instanceId) => {
    handleCloseDebug();
    elsaRunningCtl.current = window.agent.run();
    setTestStatus('Running');
    startTestInstance(aippId, version, instanceId);
  }
  // 测试轮询
  const startTestInstance = (aippId, version, instanceId) => {
    timerRef.current = setInterval(async () => {
      const res = await reTestInstance(tenantId, aippId, instanceId, version);
      if (res.code !== 0) {
        onStop(res.msg || t('debugFail'));
      }
      const runtimeData = res.data;
      if (runtimeData) {
        if (isError(runtimeData.nodeInfos)) {
          clearInterval(timerRef.current);
          setTestStatus('Error');
          elsaRunningCtl.current?.stop();
        } else if (isEnd(runtimeData.nodeInfos)) {
          clearInterval(timerRef.current);
          setTestStatus('Finished');
          elsaRunningCtl.current?.stop();
        }
        elsaRunningCtl.current?.refresh(runtimeData.nodeInfos);
        const time = (runtimeData.executeTime / 1000).toFixed(3);
        setTestTime(time);
      }
    }, 3000);
  }
  // 判断是否流程结束
  const isEnd = (nodeInfos) => {
    return nodeInfos.some((value) => value.nodeType === 'END');
  }
  // 判断是否流程出错
  const isError = (nodeInfos) => {
    return nodeInfos.some((value) => value.status === 'ERROR');
  }
  // 终止轮询
  const onStop = (content) => {
    clearInterval(timerRef.current);
    Message({ type: 'warning', content: content });
    elsaRunningCtl.current?.stop();
  }
  return <>{(
    <div>
      <Drawer title={<h5>{t('debugRun')}</h5>} open={showDebug} onClose={handleCloseDebug} width={600}
        footer={
          <Spin spinning={loading}>
            <div style={{ textAlign: 'right' }}>
              <span onClick={handleRunTest} className='run-btn'>
                <RunIcon className='run-icon' />{t('run')}
              </span>
            </div>
          </Spin>
        }
        closeIcon={
          <CloseIcon />
        }
      >
        <div className='debug'>
          <Alert message={t('debugAlert')} type='info' />
          <div className='debug-header'>
            <StartIcon className='header-icon' />
            <span className='header-title'>{t('startNode')}</span>
          </div>
          <Form
            form={form}
            layout='vertical'
            className='debug-form'
          >
            {debugTypes.map((debugType, index) => {
              return (
                <RenderFormItem
                  type={debugType.type}
                  name={debugType.name}
                  key={index}
                  isRequired={debugType.isRequired} />
              )
            })}
          </Form>
        </div>
      </Drawer>
      {/* 表单抽屉 */}
      <Drawer title={<h5>{t('manualForm')}</h5>} open={open} onClose={() => setOpen(false)} width={800}>
        <div>
          <RuntimeForm formConfig={formConfig} confirmCallBack={() => setOpen(false)} />
        </div>
      </Drawer>
    </div>
  )}</>
};


export default Index;
