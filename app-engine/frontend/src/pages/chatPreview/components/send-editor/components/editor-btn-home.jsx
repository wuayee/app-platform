
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
import robot from "@assets/images/ai/robot1.png";

// 操作按钮
const EditorBtnHome = (props) => {
  const { aippInfo, setOpen, clear, fileCallBack } = props;
  const { chatRunning, tenantId, appId } = useContext(AippContext);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ openStar, setOpenStar ] = useState(false);
  const [ showAt, setShowAt ] = useState(false);
  const [ appName, setAppName ] = useState('');
  const [ appIcon, setAppIcon ] = useState(robot);
  const [ isAt, setIsAt ] = useState(false);
  let modalRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    })
    if (aippInfo.attributes?.icon) {
      setAppIcon(aippInfo.attributes.icon);
      setAppName(aippInfo.name || '应用');
    }
  }, [props]);

  // 新聊天
  const handleOk = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    clear('all');
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
    console.log(item);
    navigate(`/app-develop/${tenantId}/chat/28be0a14e1504e218917f31db1396122`)
    setOpenStar(false);
  }
  // 多模态上传文件
  const uploadClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    modalRef.current.showModal();
  }
  // 上传文件回调
  const fileSend = (data, type) => {
    fileCallBack(data, type);
  }
  // 清空聊天记录
  const clearAllModal = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    };
    setIsModalOpen(true);
  }
  // 打开历史会话
  const openHistory = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    };
    setOpen(true);
  }
  return <>{(
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={appIcon} alt="" />
          <div className={['switch-app', isAt ? 'switch-active' : null ].join(' ')} onClick={showMoreClick}>
            { isAt && <span style={{ marginLeft: '6px' }}>正在跟</span> }
            <span className="item-name" title={appName}>{appName}</span>
            <ArrowDownIcon className="arrow-icon" />
            { isAt && <span style={{ marginLeft: '6px' }}>对话</span> }
          </div>
          <LinkIcon onClick={uploadClick} />
          { !isAt && <AtIcon onClick={atClick} /> }
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
              <ClearChatIcon style={{ marginTop: '6px' }} onClick={clearAllModal} />
              <HistoryIcon  onClick={openHistory}/>
              <span className="item-clear" onClick={() => clear()}>+ 新聊天</span>
            </div>
          )
        }
        
      </div>
      { showAt && <ReferencingApp atItemClick={atItemClick} atClick={showMoreClick}/> }
      <Modal 
        title="确认清空当前聊天" 
        open={isModalOpen} 
        onOk={handleOk} 
        onCancel={() => setIsModalOpen(false)} 
        centered>
        <span>清空后当前窗口聊天内容将不会被系统保存。</span>
      </Modal>
      <StarApps 
        open={openStar} 
        setOpen={setOpenStar} 
        handleAt={atItemClick}
        chatClick={chatClick}
      />
      <UploadFile  modalRef={modalRef} fileSend={fileSend}/>
    </div>
  )}</>
}


export default EditorBtnHome;
