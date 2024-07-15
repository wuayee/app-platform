import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate } from 'react-router-dom';
import { LeftArrowIcon, EditIcon, UploadIcon } from '@assets/icon';
import { Message } from "../../shared/utils/message";
import PublishModal from './publish-modal.jsx';
import EditModal from './edit-modal.jsx';
import knowledgeBase from '../../assets/images/knowledge/knowledge-base.png';
import TestStatus from "./test-status.jsx";
import TestModal from "./test-modal";
import TimeLineDrawer from '../../components/timeLine';
import './styles/header.scss'

const ChoreographyHead = (props) => {
  const { 
    showElsa, appInfo,
    updateAippCallBack,
    mashupClick, status,
    openDebug, testTime,
    testStatus, showTime
  } = props;
  const [ currentTime, setCurrentTime ] = useState('');
  const [ open, setOpen ] = useState(false);
  const [ versionList, setVersionList ] = useState([]);
  let modalRef = React.createRef();
  let editRef = React.createRef();
  let testRef = React.createRef();
  const { tenantId, appId } = useParams();
  const navigate = useNavigate();
  useEffect(() => {
    showTime && getCurrentTime();
  }, [appInfo])
  // 编辑名称
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  // 编辑基本信息
  function modalClick() {
    modalRef.current.showModal();
  }
  // 返回编排页面
  const backClick = () => {
    showElsa && mashupClick();
  }
  // 打开调试抽屉
  const handleOpenDebug = () => {
    openDebug();
  }

  const getCurrentTime = () => {
    let str = new Date().toTimeString().substring(0, 8);
    setCurrentTime(str);
  }
  const chatClick = () => {
    navigate(`/app-develop/${tenantId}/chat/${appId}`);
  }
  return <>{(
    <div className="app-header">
      <div className="logo">
        { showElsa && <LeftArrowIcon className="back-icon" onClick={backClick}/> }
        { appInfo?.attributes?.icon ?
          <img src={appInfo.attributes?.icon} onClick={backClick} /> :
          <img src='/src/assets/images/knowledge/knowledge-base.png' onClick={backClick}/>
        }
        <span className="header-text" title={appInfo?.name}>{ appInfo?.name }</span>
        <img className="edit-icon" src='/src/assets/images/ai/edit.png' onClick={ handleEditClick } />
        {/* {
          appInfo.state === 'active' ?
          (
            <div className="status-tag">
              <img src='/src/assets/images/ai/complate.png' />
              <span>已发布</span>
              <span className="version">V{appInfo.version}</span>
            </div>
          ) :
          (
            <div className="status-tag">
              <img src='/src/assets/images/ai/publish.png' />
              <span>未发布</span>
            </div>
          )
        } */}
        { showTime && <span>自动保存：{currentTime}</span> }
        {showElsa && <TestStatus testTime={testTime} testStatus={testStatus}/>}
      </div>
      <div className="header-grid">
        {/* <span className="history" onClick={versionDetail}>
          <img src='/src/assets/images/ai/time.png' />
        </span> */}
        <span className="history robot" onClick={chatClick}><img src='/src/assets/images/ai/robot.png' />去聊天</span>
        { showElsa && <span className="header-btn test-btn" onClick={handleOpenDebug}>调试</span> }
        { !status && <span className="header-btn" onClick={modalClick}><UploadIcon />发布</span>  }
      </div>
      <PublishModal modalRef={modalRef} appInfo={appInfo} publishType="app" />
      <EditModal modalRef={editRef} appInfo={appInfo} updateAippCallBack={updateAippCallBack}/>
      <TimeLineDrawer open={open} setOpen={setOpen} list={versionList} />
      <TestModal testRef={testRef} handleDebugClick={openDebug} type="edit"/>
    </div>
  )} </>;
};

export default ChoreographyHead;
