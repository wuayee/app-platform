
import React, { useEffect, useState, useContext, useRef } from 'react';
import { Modal } from "antd";
import { Message } from "@shared/utils/message";
import { CloseOutlined } from "@ant-design/icons";
import { 
  LinkIcon, 
  AtIcon, 
  HistoryIcon, 
  ArrowDownIcon,
  ClearChatIcon } from '@/assets/icon';
import { clearInstance } from '@shared/http/aipp';
import ReferencingApp from './referencing-app';
import UploadFile from './upload-file';
import StarApps from "../../star-apps";
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import HistoryChatDrawer from '../../history-chat';
import { useAppDispatch, useAppSelector } from '../../../../../store/hook';
import { setChatId, setChatList, setChatRunning, setOpenStar } from '../../../../../store/chatStore/chatStore';

// 操作按钮,聊天界面下面操作框
const EditorBtnHome = (props) => {
  const { fileCallBack } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const chatType = useAppSelector((state) => state.chatCommonStore.chatType);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
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
    if (appInfo.attributes?.icon) {
      setAppIcon(appInfo.attributes.icon);
    }
    setAppName(appInfo.name || '应用');
  }, [appInfo]);

  // 清空历史记录
  const handleOk = async () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    if (!chatList.length) {
      setIsModalOpen(false);
      return;
    }
    const res = await clearInstance(tenantId, appId, 'preview');
    if (res.code === 0) {
      dispatch(setChatList([]));
    }
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
    setAppName(appInfo.name);
    setIsAt(false);
  }
  // @应用点击回调
  const atItemClick = (item) => {
    setAppName(item.name);
    setShowAt(false);
    setIsAt(true);
    dispatch(setOpenStar(false));
  }
  // 更多应用
  const showMoreClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    setShowAt(false);
    dispatch(setOpenStar(true));
  }
  // 多模态上传文件
  const uploadClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    openUploadRef.current.showModal();
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

  return (
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={appIcon} alt="" />
          <div className={['switch-app', isAt ? 'switch-active' : null ].join(' ')} onClick={()=>{if(chatType==='home'){showMoreClick();}}}>
            { isAt && <span style={{ marginLeft: '6px' }}>正在跟</span> }
            <span className="item-name" title={appName}>{appName}</span>
            { !appInfo.hideHistory && <ArrowDownIcon className="arrow-icon" /> }
            { isAt && <span style={{ marginLeft: '6px' }}>对话</span> }
          </div>
          {/* <LinkIcon onClick={uploadClick} /> */}
          { (!isAt && !appInfo.hideHistory ) && <AtIcon onClick={atClick} /> }
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
              <div hidden><ClearChatIcon style={{ marginTop: '6px' }} onClick={() => setIsModalOpen(true)} /></div>
              { !appInfo.hideHistory && <HistoryIcon  onClick={(e) => {setOpenHistorySignal(e.timeStamp)}}/> }
              <span className="item-clear" onClick={() => {
                dispatch(setChatRunning(false));
                dispatch(setChatId(null));
                dispatch(setChatList([]));
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
    <UploadFile  openUploadRef={openUploadRef} fileSend={fileSend}/>
    <HistoryChatDrawer openHistorySignal={openHistorySignal}/>
    </div>
  );
}


export default EditorBtnHome;
