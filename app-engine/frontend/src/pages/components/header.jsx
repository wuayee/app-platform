import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from 'react-router-dom';
import { LeftArrowIcon } from '@assets/icon';
import PublishModal from './publish-modal.jsx';
import EditModal from './edit-modal.jsx';
import TimeLineDrawer from '../../components/timeLine';
import './styles/header.scss'

const ChoreographyHead = (props) => {
  const { 
    showElsa, 
    appInfo,
    updateAippCallBack,
    mashupClick,
    showTime
  } = props;
  const [ currentTime, setCurrentTime ] = useState('');
  const [ open, setOpen ] = useState(false);
  const [ versionList, setVersionList ] = useState([]);
  let modalRef = React.createRef();
  let editRef = React.createRef();
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
  const modalClick = () => {
    modalRef.current.showModal();
  }
  // 返回编排页面
  const backClick = () => {
    showElsa && mashupClick();
  }
  const getCurrentTime = () => {
    let str = new Date().toTimeString().substring(0, 8);
    setCurrentTime(str);
  }
  const chatClick = () => {
    navigate(`/app-develop/${tenantId}/chat/${appId}`);
  }
  const versionDetail = () => {
    setOpen(true);
    setVersionList([
      { name: '1.0.7', desc: '这个版本优化了llm节点，使大模型可以快速回答出精准的内容。', user: '林靖峰', time: '2024-06-07 14:23:12' },
      { name: '1.0.7', desc: '这个版本优化了llm节点，使大模型可以快速回答出精准的内容。', user: '林靖峰', time: '2024-06-07 14:23:12' },
      { name: '1.0.7', desc: '这个版本优化了llm节点，使大模型可以快速回答出精准的内容。', user: '林靖峰', time: '2024-06-07 14:23:12' }
    ]);
  }
  return <>{(
    <div className="app-header">
      <div className="logo">
        { showElsa && <LeftArrowIcon className="back-icon" onClick={backClick}/> }
        { appInfo?.attributes?.icon ?
          <img src={appInfo.attributes.icon} onClick={backClick} /> : 
          <img src='/src/assets/images/knowledge/knowledge-base.png' onClick={backClick}/> 
        }
        <span className="header-text" title={appInfo?.name}>{ appInfo?.name }</span>
        <img className="edit-icon" src='/src/assets/images/ai/edit.png' onClick={ handleEditClick } />
        {
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
        }
        { showTime && <span>自动保存：{currentTime}</span> }
      </div>
      <div className="header-grid">
        {/* <span className="history" onClick={versionDetail}>
          <img src='/src/assets/images/ai/time.png' />
        </span> */}
        { appInfo.state === 'active' && <span className="history robot" onClick={chatClick}><img src='/src/assets/images/ai/robot.png' />去聊天</span> }
        <span className="header-btn" onClick={modalClick}>发布</span>
      </div>
      <PublishModal modalRef={modalRef} appInfo={appInfo} publishType="app" />
      <EditModal modalRef={editRef} appInfo={appInfo} updateAippCallBack={updateAippCallBack}/>
      <TimeLineDrawer open={open} setOpen={setOpen} list={versionList} />
    </div>
  )} </>;
};

export default ChoreographyHead;
