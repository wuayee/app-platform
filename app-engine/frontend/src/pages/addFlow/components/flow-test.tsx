
import React, { useContext, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Drawer, Form } from 'antd';
import { StartIcon, CloseIcon, RunIcon } from '@assets/icon';
import { Message } from '@shared/utils/message';
import { FlowContext } from '../../aippIndex/context';
import { startInstance, reTestInstance } from '@shared/http/aipp';
import RenderFormItem from './render-form-item';

const Index = (props) => {
  const { 
    debugTypes,
    setTestTime,
    setTestStatus,
    setShowDebug,
    showDebug,
    appRef
  } = props;
  const { type, appInfo } = useContext(FlowContext);
  const { tenantId, appId } = useParams();
  const elsaRunningCtl = useRef();
  const [form] = Form.useForm();
  const timerRef = useRef(null);
  // 关闭测试抽屉
  const handleCloseDebug = () => {
    setShowDebug(false);
  }
  const handleRunTest = () => {
    elsaRunningCtl.current?.reset();
    setTestStatus(null);
    setTestTime(0);
    form.validateFields().then((values) => {
      handleRun(values);
      handleCloseDebug();
    })
      .catch((errorInfo) => {
        Message({type: 'warning', content: '请输入必填项'});
      });
  }
  // 点击运行
  const handleRun = async (values) => {
    let appDto = type ? appInfo : appRef.current;
    const params = {
      appDto,
      context: {
        initContext: values
      }
    };
    elsaRunningCtl.current = window.agent.run();
    const res = await startInstance(tenantId, appId, params);
    if (res.code === 0) {
      const {aippCreate, instanceId} = res.data;
      setTestStatus('Running');
      // 调用轮询
      startTestInstance(aippCreate.aippId, aippCreate.version, instanceId);
    } else {
      elsaRunningCtl.current && elsaRunningCtl.current.reset();
    }
  }
  // 测试轮询
  const startTestInstance = (aippId, version, instanceId) => {
    timerRef.current = setInterval(async () => {
      const res = await reTestInstance(tenantId, aippId, instanceId, version);
      if (res.code !== 0) {
        onStop( res.msg || '测试失败');
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
    }, 1000);
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
      <Drawer title={<h5>测试运行</h5>} open={showDebug} onClose={handleCloseDebug} width={600}
          footer={
            <div style={{ textAlign: 'right' }}>
              <span onClick={handleRunTest} className='run-btn'>
                <RunIcon className='run-icon'/>运行
              </span>
            </div>
          }
          closeIcon={
            <CloseIcon />
          }
      >
        <div className='debug'>
          <div className='debug-header'>
            <StartIcon className='header-icon' />
            <span className='header-title'>开始节点</span>
          </div>
          <Form
            form={form}
            layout='vertical'
            className='debug-form'
          >
            {debugTypes.map((debugType, index) => {
              return (
                <RenderFormItem type={debugType.type} name={debugType.name} key={index} isRequired={debugType.isRequired}/>
              )
            })}
          </Form>
        </div>
      </Drawer>
    </div>
  )}</>
};


export default Index;
