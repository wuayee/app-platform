
import React, { useEffect, useCallback, useState, useRef, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { JadeFlow } from '@fit-elsa/elsa-react';
import { debounce } from '@shared/utils/common';
import { updateFlowInfo } from '@shared/http/aipp';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import { Message } from '@shared/utils/message';
import { useAppDispatch } from '../../../store/hook';
import { setAppInfo } from '../../../store/appInfo/appInfo';
import HuggingFaceModal from './hugging-face-modal';
import { FlowContext } from '../../aippIndex/context';
import { configMap } from '../config';
import { Button, Alert } from "antd";

const Stage = (props) => {
  const { setDragData, setTestStatus, showFlowChangeWarning, setShowFlowChangeWarning } = props;
  const [ showModal, setShowModal ] = useState(false);
  const [ taskName, setTaskName ] = useState('');
  const [ selectModal, setSelectModal ] = useState('');
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  const { type, appInfo, setFlowInfo, setShowTime } = useContext(FlowContext);
  const { tenantId, appId } = useParams();
  const modelCallback = useRef<any>();
  const currentApp = useRef<any>();
  const render = useRef(false);
  const change = useRef(false);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (appInfo.name && !render.current) {
      currentApp.current = JSON.parse(JSON.stringify(appInfo));
      window.agent = null;
      setElsaData();
    }
  }, [appInfo]);

  // 编辑工作流
  function setElsaData() {
    let graphData = appInfo.flowGraph?.appearance || {};
    const stageDom = document.getElementById('stage');
    let data = JSON.parse(JSON.stringify(graphData));
    let configIndex = CONFIGS.findIndex(item => item.node === 'llmNodeState');
    CONFIGS[configIndex].params.tenantId = tenantId;
    CONFIGS[configIndex].params.appId = appId;
    const importFiles = [
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/fileContentComponent.jsx`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/interviewQuestionsComponent.jsx`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/manageCubeCreateReportComponent.jsx`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/QuestionClar/questionClarComponent`),
      () => import(/* webpackIgnore: true */`../../chatPreview/components/runtimeForm/conditionForm/conditionFormComponent`),
    ];
    JadeFlow.edit(stageDom, tenantId, data, CONFIGS, importFiles).then(agent => {
      window.agent ? null : window.agent = agent;
      render.current = true;
      agent.onChange((dirtyAction) => {
        handleChange(dirtyAction);
      });
      agent.onModelSelect(({ taskName, selectedModel, onSelect }) => {
        setSelectModal(selectedModel);
        setTaskName(taskName.trim());
        modelCallback.current = onSelect;
        setShowModal(true);
      })
    })
    getAddFlowConfig(tenantId, {pageNum: 1, pageSize: 20, tag: 'Builtin',version:''}).then(res => {
      if (res.code === 0) {
        setDragData(res.data);
      }
    });
  }
  // hugging-face模型选中
  const onModelSelectCallBack = (model) => {
    modelCallback.current(model);
  }
  // 数据实时保存
  const handleChange = useCallback(debounce((dirtyAction) => elsaChange(dirtyAction), 2000), []);
  function elsaChange(dirtyAction) {
    if (dirtyAction.action === 'jade_node_config_change') {
      setTestStatus(null);
      localStorage.getItem('showFlowChangeWarning') !== 'false' && setShowFlowChangeWarning(true);
    }
    let graphChangeData = window.agent.serialize();
    currentApp.current.flowGraph.appearance = graphChangeData;
    window.agent.validate().then(() => {
      updateAppRunningFlow();
    }).catch((err) => {
      let str = typeof(err) === 'string' ? err : '请输入流程必填项';
      Message({ type: 'warning', content: str});
    });
  }
  // 编辑更新应用
  async function updateAppRunningFlow() {
    const res = await updateFlowInfo(tenantId, appId, currentApp.current.flowGraph);
    if (res.code === 0) {
      if (type) {
        dispatch(setAppInfo(JSON.parse(JSON.stringify(appInfo))));
      } else {
        setFlowInfo(currentApp.current);
      }
      setShowTime(true);
      Message({ type: 'success', content: type ? '高级配置更新成功': '工具流更新成功' });
    }
  }
  // 拖拽完成回调
  function handleDragEnter(e) {
    const nodeTab = e.dataTransfer.getData('itemTab');
    let nodeType = e.dataTransfer.getData('itemType');
    let nodeMetaData = JSON.parse(e.dataTransfer.getData('itemMetaData'));
    switch (nodeTab) {
      case 'basic':
        window.agent.createNode(nodeType, e, { uniqueName: nodeMetaData?.uniqueName });
        break;
      case 'tool':
        window.agent.createNode(nodeType, e, nodeMetaData);
        break;
      default:
        break;
    }
  }

  // 点击“不再提示”按钮回调
  const handleClickNoMoreTips = () => {
    localStorage.setItem('showFlowChangeWarning', 'false');
    setShowFlowChangeWarning(false);
  }

  // 点击关闭流程配置修改后告警回调
  const handleCloseFlowChangeWarningAlert = () => {
    setShowFlowChangeWarning(false);
  }

  return <>
    <div
      className='content-right'
      onDragOver={(e) => e.preventDefault()}
      onDrop ={handleDragEnter}>
        <div className='elsa-canvas' id='stage'></div>
    </div>
    <HuggingFaceModal
      showModal={showModal}
      setShowModal={setShowModal}
      onModelSelectCallBack={onModelSelectCallBack}
      taskName={taskName}
      selectModal={selectModal}
    />
    {showFlowChangeWarning && <Alert
      className='flow-change-warning-content'
      message=""
      description="你已经修改了工作流，需要重新调试成功才可以发布。"
      type="info"
      onClose={handleCloseFlowChangeWarningAlert}
      action={
          <Button size="small" type="link" onClick={handleClickNoMoreTips}>
            不再提示
          </Button>
      }
      closable
    />}
  </>
};

export default Stage;
