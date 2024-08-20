
import React, { useEffect, useCallback, useState, useRef, useContext } from 'react';
import { Button, Alert } from 'antd';
import { useParams } from 'react-router-dom';
import { JadeFlow } from '@fit-elsa/elsa-react';
import AddKnowledge from '../../configForm/configUi/components/add-knowledge';
import HuggingFaceModal from './hugging-face-modal';
import ToolModal from './tool-modal';
import { debounce } from '@shared/utils/common';
import { updateFlowInfo } from '@shared/http/aipp';
import { getAddFlowConfig } from '@shared/http/appBuilder';
import { Message } from '@shared/utils/message';
import { useAppDispatch } from '@/store/hook';
import { setAppInfo } from '@/store/appInfo/appInfo';
import { FlowContext } from '../../aippIndex/context';
import { configMap } from '../config';
import { useTranslation } from 'react-i18next';

const Stage = (props) => {
  const { t } = useTranslation();
  const { setDragData, setTestStatus, showFlowChangeWarning, setShowFlowChangeWarning } = props;
  const [ showTools, setShowTools ] = useState(false);
  const [ showModal, setShowModal ] = useState(false);
  const [ taskName, setTaskName ] = useState('');
  const [ selectModal, setSelectModal ] = useState('');
  const [ skillList, setSkillList ] = useState([]);
  const { CONFIGS } = configMap[process.env.NODE_ENV];
  const { type, appInfo, setFlowInfo, setShowTime } = useContext(FlowContext);
  const { tenantId, appId } = useParams();
  const modelCallback = useRef<any>();
  const knowledgeCallback = useRef<any>();
  const pluginCallback = useRef<any>();
  const currentApp = useRef<any>();
  const render = useRef(false);
  const modalRef = useRef();
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
    JadeFlow.edit(stageDom, tenantId, data, CONFIGS, i18n, importFiles).then(agent => {
      window.agent ? null : window.agent = agent;
      render.current = true;
      agent.onChange((dirtyAction) => {
        if (dirtyAction.action === 'jade_node_config_change') {
          setTestStatus(null);
          localStorage.getItem('showFlowChangeWarning') !== 'false' && setShowFlowChangeWarning(true);
        }
        handleChange();
      });
      // huggingface模态框
      agent.onModelSelect(({ taskName, selectedModel, onSelect }) => {
        setSelectModal(selectedModel);
        setTaskName(taskName.trim());
        modelCallback.current = onSelect;
        setShowModal(true);
      })
    })
    getAddFlowConfig(tenantId, { pageNum: 1, pageSize: 20, tag: 'Builtin', version: '' }).then(res => {
      if (res.code === 0) {
        setDragData(res.data);
      }
    });
  }
  // hugging-face模型选中
  const onModelSelectCallBack = (model) => {
    modelCallback.current(model);
  }
  // 知识库选中
  const handleKnowledgeChange = (value) => {
    knowledgeCallback.current(value);
  }
  // 插件工具流选中
  const toolsConfirm = (checkedList) => {
    let arr = checkedList.map(item => {
      let uri = '';
      if (item.type === 'workflow') {
        if (item.appId.length) {
          uri = `${location.origin}/#/app-develop/${tenantId}/app-detail/flow-detail/${item.appId}`;
        }
      } else {
        uri = `${location.origin}/#/plugin/detail-flow/${item.uniqueName}`;
      }
      return {
        uniqueName: item.uniqueName,
        tags: item.tags,
        name: item.name,
        uri,
        version: item.version
      };
    });
    pluginCallback.current(arr);
  }
  // 数据实时保存
  const handleChange = useCallback(debounce(() => elsaChange(), 2000), []);
  function elsaChange() {
    let graphChangeData = window.agent.serialize();
    currentApp.current.flowGraph.appearance = graphChangeData;
    window.agent.validate().then(() => {
      updateAppRunningFlow();
    }).catch((err) => {
      let str = typeof (err) === 'string' ? err : t('plsEnterFlowRequiredItem');
      Message({ type: 'warning', content: str });
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
      Message({ type: 'success', content: type ? t('graphUpdateSuccess') : t('flowUpdateSuccess') });
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
      onDrop={handleDragEnter}>
      <div className='elsa-canvas' id='stage'></div>
    </div>
    <HuggingFaceModal
      showModal={showModal}
      setShowModal={setShowModal}
      onModelSelectCallBack={onModelSelectCallBack}
      taskName={taskName}
      selectModal={selectModal}
    />
    <AddKnowledge
      modalRef={modalRef}
      tenantId={tenantId}
      handleDataChange={handleKnowledgeChange}
    />
    <ToolModal
      showModal={showTools}
      setShowModal={setShowTools}
      toolsConfirm={toolsConfirm}
      checkData={skillList}
      modalType='mashup'
      type='addSkill'
    />
    {showFlowChangeWarning && <Alert
      className='flow-change-warning-content'
      message=''
      description={t('flowChangeWarningContent')}
      type='info'
      onClose={handleCloseFlowChangeWarningAlert}
      action={
        <Button size='small' type='link' onClick={handleClickNoMoreTips}>
          {t('noMoreTips')}
        </Button>
      }
      closable
    />}
  </>
};

export default Stage;
