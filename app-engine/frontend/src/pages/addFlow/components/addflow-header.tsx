
import React, { useEffect, useState, useImperativeHandle, useRef, useContext } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { EditIcon, LeftArrowIcon, UploadIcon } from '@assets/icon';
import { updateAppInfo } from '@shared/http/aipp';
import { Message } from '@shared/utils/message';
import { FlowContext } from '../../aippIndex/context';
import EditTitleModal from '../../components/edit-title-modal';
import PublishModal from '../../components/publish-modal';
import TestModal from "../../components/test-modal";
import TestStatus from "../../components/test-status";
import FlowTest from './flow-test';

const AddHeader = (props) => {
  const { addId,  appRef, flowIdRef } = props;
  const { type, appInfo, modalInfo, setModalInfo } = useContext(FlowContext);
  const [ waterFlowName, setWaterFlowName ] = useState('无标题');
  const [ debugTypes, setDebugTypes ] = useState([]);
  const { tenantId, appId } = useParams();
  const [ showDebug, setShowDebug ] = useState(false);
  const [ isTested, setIsTested ] = useState(false);
  const [ testStatus, setTestStatus ] = useState('Running');
  const [ isTesting, setIsTesting ] = useState(false);
  const [ testTime, setTestTime ] = useState(0);
  let editRef:any = useRef(null);
  let modalRef:any = useRef(null);
  let testRef:any = useRef(null);
  
  const navigate = useNavigate();
  // 发布工具流
  const handleUploadFlow = () => {
    if (!isTested) {
      testRef.current.showModal();
      return;
    }
    modalRef.current.showModal();
  }
  // 编辑工具流
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  // 返回上一页
  const handleBackClick = () => {
    navigate(-1);
  }
  // 保存回调
  function onFlowNameChange(params) {
    setModalInfo(() => {
      appRef.current.name = params.name;
      appRef.current.attributes.description = params.description;
      let list = JSON.parse(JSON.stringify(appRef.current))
      return list;
    })
    updateAppWorkFlow('waterFlow');
  }
   // 创建更新应用
   async function updateAppWorkFlow(optionType = '') {
    let id = type ? appId : flowIdRef.current;
    const res = await updateAppInfo(tenantId, id, appRef.current);
    if (res.code === 0) {
      setWaterFlowName(appRef.current.name);
      optionType && editRef.current.handleCancel();
    } else {
      optionType && editRef.current.handleLoading();
    }
  }
  // 测试
  const handleDebugClick = () => {
    window.agent.validate().then(()=> {
      setDebugTypes(window.agent.getFlowRunInputMetaData());
      setShowDebug(true);
    }).catch(err => {
      let str = typeof(err) === 'string' ? err : '请输入流程必填项';
      Message({ type: "warning", content: str});
    })
  }
  return <>{(
    <div>
      <div className='app-header'>
        <div className='logo'>
          <LeftArrowIcon className="icon-back" onClick={ handleBackClick } />
          <span className='header-text' title={waterFlowName}>{ waterFlowName }</span>
          <span className='header-edit'>
            <EditIcon onClick={ handleEditClick } />
          </span>
          < TestStatus isTested={isTested} isTesting={isTesting} testTime={testTime} testStatus={testStatus}/>
        </div>
        <div className='header-grid'>
          <span className="header-btn test-btn" onClick={handleDebugClick}>测试</span>
          <span className="header-btn" onClick={handleUploadFlow}><UploadIcon />发布</span>
        </div>
      </div>
      <PublishModal
        modalRef={modalRef}
        appInfo={appInfo || appRef.current}
        waterFlowName={waterFlowName}
        modalInfo={modalInfo}
        addId={addId}
        publishType="waterflow"
      />
      <TestModal
        testRef={testRef}
        handleDebugClick={handleDebugClick}
      />
      <EditTitleModal
        modalRef={editRef}
        onFlowNameChange={onFlowNameChange}
        waterFlowName={waterFlowName}
        modalInfo={modalInfo}
      />
      <FlowTest 
        setIsTested={setIsTested} 
        setTestStatus={setTestStatus}
        setIsTesting={setIsTesting}
        setTestTime={setTestTime}
        setShowDebug={setShowDebug}
        showDebug={showDebug}
        debugTypes={debugTypes}
        appRef={appRef}
      />
    </div>
  )}</>
};


export default AddHeader;
