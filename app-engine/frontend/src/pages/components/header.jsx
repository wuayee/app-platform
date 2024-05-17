import React, { useEffect, useState } from "react";
import { LeftArrowIcon, EditIcon, UploadIcon } from '@assets/icon';
import PublishModal from './publish-modal.jsx';
import EditModal from './edit-modal.jsx';
import robot from '../../assets/images/ai/robot1.png';

const Head = (props) => {
  const { showElsa, aippInfo, updateAippCallBack, mashupClick } = props;
  const [aippEdit, setAippEdit] = useState(false);
  let modalRef = React.createRef();
  let editRef = React.createRef();
  useEffect(() => {
    setAippEdit(true);
  }, [props.aippInfo])
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

  return <>{(
    <div className="header">
      <div className="logo">
        { showElsa && <LeftArrowIcon className="back-icon" onClick={mashupClick}/> }
        { aippInfo?.attributes?.icon ? <img src={aippInfo.attributes.icon} onClick={backClick} /> : <img src={robot} onClick={backClick}/> }
        <span className="header-text">{ aippInfo?.name }</span>
        {
          aippEdit && <EditIcon onClick={ handleEditClick } />
        }
      </div>
      <div className="header-user">
        { aippEdit && <span className="header-btn" onClick={modalClick}><UploadIcon />发布</span>  }
      </div>
      <PublishModal modalRef={modalRef} aippInfo={aippInfo} publishType="app" />
      <EditModal modalRef={editRef} aippInfo={aippInfo} updateAippCallBack={updateAippCallBack}/>
    </div>
  )} </>;
};

export default Head;
