import React, {useRef} from "react";
import { LeftArrowIcon, EditIcon, UploadIcon } from '@assets/icon';
import { Message } from "../../shared/utils/message";
import PublishModal from './publish-modal.jsx';
import EditModal from './edit-modal.jsx';
import knowledgeBase from '../../assets/images/knowledge/knowledge-base.png';
import TestStatus from "./test-status.jsx";
import TestModal from "./test-modal";

const ChoreographyHead = (props) => {
  const { 
    showElsa, appInfo,
    updateAippCallBack,
    mashupClick, status,
    openDebug, testTime,
    testStatus } = props;
  let modalRef = React.createRef();
  let editRef = React.createRef();
  let testRef = React.createRef();
  // 编辑名称
  const handleEditClick = () => {
    editRef.current.showModal();
  }
  // 编辑基本信息
  function modalClick() {
    modalRef.current.showModal();
  }
  // 返回编排页面
  function backClick() {
    showElsa && mashupClick();
  }
  // 打开调试抽屉
  const handleOpenDebug = () => {
    openDebug();
  }

  return <>{(
    <div className="header">
      <div className="logo">
        { showElsa && <LeftArrowIcon className="back-icon" onClick={backClick}/> }
        { appInfo?.attributes?.icon ? <img src={appInfo.attributes.icon} onClick={backClick} /> : <img src={knowledgeBase} onClick={backClick}/> }
          <span className="header-text">{ appInfo?.name }</span>
        {
          !status && <EditIcon onClick={ handleEditClick } />
        }
        {showElsa && <TestStatus testTime={testTime} testStatus={testStatus}/>}
      </div>
      <div className="header-grid">
        { showElsa && <span className="header-btn test-btn" onClick={handleOpenDebug}>调试</span> }
        { !status && <span className="header-btn" onClick={modalClick}><UploadIcon />发布</span>  }
      </div>
      <PublishModal modalRef={modalRef} appInfo={appInfo} publishType="app" />
      <EditModal modalRef={editRef} appInfo={appInfo} updateAippCallBack={updateAippCallBack}/>
      <TestModal testRef={testRef} handleDebugClick={openDebug} type="edit"/>
    </div>
  )} </>;
};

export default ChoreographyHead;
