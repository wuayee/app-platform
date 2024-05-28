import React from "react";
import { LeftArrowIcon, EditIcon, UploadIcon } from '@assets/icon';
import { Message } from "../../shared/utils/message";
import PublishModal from './publish-modal.jsx';
import EditModal from './edit-modal.jsx';
import robot from '../../assets/images/ai/robot1.png';

const Head = (props) => {
  const { showElsa, aippInfo, updateAippCallBack, mashupClick, status, chatRunning } = props;
  let modalRef = React.createRef();
  let editRef = React.createRef();
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
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中，请稍后再试' })
      return
    }
    showElsa && mashupClick();
  }

  return <>{(
    <div className="header">
      <div className="logo">
        { showElsa && <LeftArrowIcon className="back-icon" onClick={backClick}/> }
        { aippInfo?.attributes?.icon ? <img src={aippInfo.attributes.icon} onClick={backClick} /> : <img src={robot} onClick={backClick}/> }
        <span className="header-text">{ aippInfo?.name }</span>
        { !status && <EditIcon onClick={ handleEditClick } /> }
        { !status ? <span style={{ paddingLeft: '16px' }}>未发布</span> : <span style={{ paddingLeft: '16px' }}>已发布</span>}
      </div>
      <div className="header-user">
        { aippInfo?.updateAt && <span style={{ paddingRight: '16px' }}>更新时间：{ aippInfo.updateAt }</span> }
        { !status && <span className="header-btn" onClick={modalClick}><UploadIcon />发布</span>  }
      </div>
      <PublishModal modalRef={modalRef} aippInfo={aippInfo} publishType="app" />
      <EditModal modalRef={editRef} aippInfo={aippInfo} updateAippCallBack={updateAippCallBack}/>
    </div>
  )} </>;
};

export default Head;
