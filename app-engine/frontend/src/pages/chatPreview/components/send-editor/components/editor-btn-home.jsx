
import React, { useEffect, useState, useContext, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tooltip, Dropdown, Space, Modal } from "antd";
import { Message } from "@shared/utils/message";
import { AippContext } from '@/pages/aippIndex/context';
import { CloseOutlined } from "@ant-design/icons";
import { 
  LinkIcon, 
  AtIcon, 
  HistoryIcon, 
  ArrowDownIcon, 
  LanguagesIcon, 
  ClearChatIcon } from '@/assets/icon';
import ReferencingApp from './referencing-app';
import UploadFile from './upload-file';
import StarApps from "../../star-apps";
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import LinkFile from './file-preview';
import HistoryChatDrawer from '../../history-chat';

// 操作按钮,聊天界面下面操作框
const EditorBtnHome = () => {
  const { chatRunning, tenantId, appId,aippInfo ,setOpenStar,
    setChatList,setChatId,setChatRunning,setClearChat,chatType} = useContext(AippContext);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ showAt, setShowAt ] = useState(false);
  const [ appName, setAppName ] = useState('');
  const [ appIcon, setAppIcon ] = useState(knowledgeBase);
  const [ isAt, setIsAt ] = useState(false);
  const [openHistorySignal,setOpenHistorySignal]=useState(null);

  let openUploadRef = useRef(null);
  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    })
    if (aippInfo.attributes?.icon) {
      setAppIcon(aippInfo.attributes.icon);
    }
    setAppName(aippInfo.name || '应用');
  }, [aippInfo]);

  // 清空聊天
  const handleOk = (time) => {
    setClearChat(time);
    setIsModalOpen(false);
  };
  // @ 应用点击
  const atClick = (e) => {
    e.stopPropagation();
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    setShowAt(!showAt);
  }
  // 取消@应用功能
  const cancleAt = () => {
    setAppName(aippInfo.name);
    setIsAt(false);
  }
  // @应用点击回调
  const atItemClick = (item) => {
    setAppName(item.name);
    setShowAt(false);
    setIsAt(true);
    setOpenStar(false)
  }
  // 更多应用
  const showMoreClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    setShowAt(false);
    setOpenStar(true)
  }
  // 开始聊天
  const chatClick = (item) => {
    setOpenStar(false);
  }
  // 多模态上传文件
  const uploadClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    openUploadRef.current.showModal();
  }
  // 清空聊天记录
  const clearAllModal = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    };
    setIsModalOpen(true);
  }
  
  return (
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={appIcon} alt="" />
          <div className={['switch-app', isAt ? 'switch-active' : null ].join(' ')} onClick={()=>{if(chatType==='home'){showMoreClick();}}}>
            { isAt && <span style={{ marginLeft: '6px' }}>正在跟</span> }
            <span className="item-name" title={appName}>{appName}</span>
            <div hidden={chatType!=='home'}><ArrowDownIcon className="arrow-icon"/></div>
            { isAt && <span style={{ marginLeft: '6px' }}>对话</span> }
          </div>
          <LinkIcon onClick={uploadClick} />

          {/* 暂时隐藏@图标 */}
          {/* { !isAt && <AtIcon onClick={atClick} /> } */}
        </div>
      </div>
      <div className="inner-right">
        { 
          isAt ? 
          (
            <div className="inner-item">
              <CloseOutlined className="item-close" onClick={cancleAt}/>
            </div>
          ) : 
          (
            <div className="inner-item">
              <div><ClearChatIcon style={{ marginTop: '6px' }} onClick={() => setIsModalOpen(true)} /></div>
              <HistoryIcon  onClick={(e) => {setOpenHistorySignal(e.timeStamp);}}/>
              <span className="item-clear" onClick={() => {
                setChatRunning(false);
                setChatId(null);
                setChatList(() => {
                  let arr = [];
                  return arr;
                });
              }}>+ 新聊天</span>
            </div>
          )
        }
      </div>
      { showAt && <ReferencingApp atItemClick={atItemClick} atClick={showMoreClick}/> }
      <Modal 
        title="确认清空当前聊天" 
        open={isModalOpen} 
        onOk={(e)=>handleOk(e.timeStamp)} 
        onCancel={() => setIsModalOpen(false)} 
        centered>
        <span>清空后当前窗口聊天内容将不会被系统保存。</span>
      </Modal>
      <StarApps 
        handleAt={atItemClick}
      />
    <LinkFile openUploadRef={openUploadRef}/>
    <HistoryChatDrawer openHistorySignal={openHistorySignal}/>
    </div>
  );
}


export default EditorBtnHome;
