
import React, { useEffect, useState, useContext, useRef } from 'react';
import {Modal, Switch} from 'antd';
import { Message } from '@shared/utils/message';
import { CloseOutlined } from '@ant-design/icons';
import { 
  LinkIcon, 
  AtIcon, 
  HistoryIcon, 
  ArrowDownIcon,
  ClearChatIcon } from '@/assets/icon';
import { clearInstance } from '@shared/http/aipp';
import ReferencingApp from './referencing-app';
import UploadFile from './upload-file';
import StarApps from '../../star-apps';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import HistoryChatDrawer from '../../history-chat';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import {
  setAtChatId,
  setChatId,
  setChatList,
  setChatRunning,
  setOpenStar
} from '@/store/chatStore/chatStore';
import { setAtAppInfo, setAtAppId } from '@/store/appInfo/appInfo';
import { getAppInfo } from '@/shared/http/aipp';
import { setUseMemory } from '@/store/common/common';
import { updateChatId } from "@/shared/utils/common";

// 操作按钮,聊天界面下面操作框
const EditorBtnHome = (props) => {
  const { fileCallBack, editorRef } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatType = useAppSelector((state) => state.chatCommonStore.chatType);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const atAppId = useAppSelector((state) => state.appStore.atAppId);
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const showMulti = useAppSelector((state) => state.commonStore.historySwitch);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ showAt, setShowAt ] = useState(false);
  const [ appName, setAppName ] = useState('');
  const [ appIcon, setAppIcon ] = useState(knowledgeBase);
  const [ openHistorySignal, setOpenHistorySignal ]=useState(null);
  const [ searchKey, setSearchKey ] = useState(null);
  let openUploadRef = useRef(null);
  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    })
    setAppIcon(appInfo.attributes?.icon);
    setAppName(appInfo.name || '应用');
  }, [appInfo]);

  useEffect(() => {
    if (atAppInfo) {
      setAppIcon(atAppInfo.attributes?.icon);
      setAppName(atAppInfo.name);
    } else {
      setAppIcon(appInfo.attributes?.icon);
      setAppName(appInfo.name || '应用');
    }
  }, [atAppInfo])

  // 检测是否输入@
  useEffect(() => {
    const handleInputAt = () => {
      const value = editorRef.current.innerText;
      if (value.startsWith('@')) {
        const contentAfterAt = value.slice(1);
        setSearchKey(contentAfterAt ? contentAfterAt : '');
        setShowAt(true);
      } else {
        setShowAt(false);
      }
    };

    editorRef.current.addEventListener('input', handleInputAt);

    return () => {
      if (editorRef.current) {
        editorRef.current.removeEventListener('input', handleInputAt);
      }
    };
  }, []);

  // 清空历史记录
  const handleOk = async () => {
    if (isChatRunning()) { return; }
    if (!chatList.length) {
      setIsModalOpen(false);
      return;
    }
    const type = appInfo.state === 'active' ? 'normal' : 'preview';
    const res = await clearInstance(tenantId, appId, type);
    if (res.code === 0) {
      dispatch(setChatList([]));
    }
    setIsModalOpen(false);
  };
  // @ 应用点击
  const atClick = (e) => {
    e.stopPropagation();
    if (isChatRunning()) { return; }
    setShowAt(!showAt);
  }
  // 取消@应用功能
  const cancelAt = () => {
    setAppName(appInfo.name);
    dispatch(setAtAppId(null));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
  }
  // @应用点击回调
  const atItemClick = async (item) => {
    const appInfoRes = await getAppInfo(tenantId, item.id);
    if (appInfoRes.code === 0) {
      dispatch(setAtAppInfo(appInfoRes.data));
    }
    dispatch(setAtAppId(item.id));
    setAppName(item.name);
    setShowAt(false);
    dispatch(setOpenStar(false));
    if (item.id !== atAppId) {
      dispatch(setAtChatId(null));
    }
  }
  // 更多应用
  const showMoreClick = () => {
    if (isChatRunning()) { return; }
    setShowAt(false);
    dispatch(setOpenStar(true));
  }
  // 多模态上传文件
  const uploadClick = () => {
    if (isChatRunning()) { return; }
    openUploadRef.current.showModal();
  }
  // 上传文件回调
  const fileSend = (data, type) => {
    fileCallBack(data, type);
  }
  // 清空聊天记录
  const clearAllModal = () => {
    if (isChatRunning()) { return; }
    setIsModalOpen(true);
  }
  //是否使用多轮对话
  const onMultiConverChange = (checked) => {
    dispatch(setUseMemory(checked));
  }

  //点击“新聊天”按钮回调
  const onClickNewChat = () => {
    dispatch(setChatRunning(false));
    updateChatId(null, appId);
    dispatch(setChatId(null));
    dispatch(setChatList([]));
    dispatch(setAtAppInfo(null));
    dispatch(setAtChatId(null));
    dispatch(setAtAppId(null));
  }

  // 点击历史对话图标回调
  const historyChatClick = (e) => {
    if (isChatRunning()) { return; }
    setOpenHistorySignal(e.timeStamp);
  }

  // 检验是否正在对话中
  const isChatRunning = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return true;
    }
    return false;
  }

  return (
    <div className='btn-inner'>
      <div className='inner-left'>
        <div className='inner-item'>
          {appIcon ? <img src={appIcon} alt='' /> : <img src={knowledgeBase} alt='' />}
          <div className={['switch-app', atAppId ? 'switch-active' : null ].join(' ')} onClick={()=>{if(chatType==='home'){showMoreClick();}}}>
            { atAppId && <span style={{ marginLeft: '6px' }}>正在跟</span> }
            <span className='item-name' title={appName}>{appName}</span>
            { !appInfo.hideHistory && <ArrowDownIcon className='arrow-icon' /> }
            { atAppId && <span style={{ marginLeft: '6px' }}>对话</span> }
          </div>
           <LinkIcon onClick={uploadClick} />
           { (!atAppId) && <AtIcon onClick={atClick} /> }
        </div>
      </div>
      <div className='inner-right'>
        { 
          atAppId ?
          (
            <div className='inner-item'>
              <CloseOutlined className='item-close' onClick={cancelAt}/>
            </div>
          ) : 
          (
            <div className='inner-item'>
              <div hidden><ClearChatIcon style={{ marginTop: '6px' }} onClick={() => setIsModalOpen(true)} /></div>
              { !appInfo.hideHistory && <HistoryIcon  onClick={historyChatClick}/> }
              {showMulti && <div className='multi-conversation-title'>
                <span>多轮对话</span>
                <Switch className='multi-conversation-switch' value={useMemory} onChange={onMultiConverChange}/>
              </div>}
              <span className='item-clear' onClick={onClickNewChat}>+ 新聊天</span>
            </div>
          )
        }
      </div>
      { showAt && <ReferencingApp atItemClick={atItemClick}
                                  atClick={showMoreClick}
                                  searchKey={searchKey}
                                  setSearchKey={setSearchKey}/> }
      <Modal 
        title='确认清空当前聊天' 
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
