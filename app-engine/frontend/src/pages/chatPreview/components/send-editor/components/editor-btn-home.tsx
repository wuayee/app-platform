
import React, { useEffect, useState, useContext, useRef } from 'react';
import { Button, Modal, Switch } from 'antd';
import { Message } from '@shared/utils/message';
import { CloseOutlined } from '@ant-design/icons';
import {
  LinkIcon,
  AtIcon,
  HistoryIcon,
  ArrowDownIcon,
  ClearChatIcon,
  ShareIcon,
  NotificationIcon
} from '@/assets/icon';
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
import { HOME_APP_ID } from '../common/config';
import { useTranslation } from 'react-i18next';

// 操作按钮,聊天界面下面操作框
const EditorBtnHome = (props) => {
  const { t } = useTranslation();
  const { fileCallBack, editorRef, chatType, setEditorShow } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const atAppId = useAppSelector((state) => state.appStore.atAppId);
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const showMulti = useAppSelector((state) => state.commonStore.historySwitch);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const dimension = useAppSelector((state) => state.commonStore.dimension);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showAt, setShowAt] = useState(false);
  const [showNotice, setShowNotice] = useState(false);
  const [notice, setNotice] = useState('');
  const [appName, setAppName] = useState('');
  const [appIcon, setAppIcon] = useState();
  const [openHistorySignal, setOpenHistorySignal] = useState(null);
  const [searchKey, setSearchKey] = useState(null);
  let openUploadRef = useRef(null);
  useEffect(() => {
    document.body.addEventListener('click', () => {
      setShowAt(false);
    })
    setAppIcon(appInfo.attributes?.icon);
    setAppName(appInfo.name || t('app'));
  }, [appInfo]);

  useEffect(() => {
    if (atAppInfo) {
      setAppIcon(atAppInfo.attributes?.icon);
      setAppName(atAppInfo.name);
    } else {
      setAppIcon(appInfo.attributes?.icon);
      setAppName(appInfo.name || t('app'));
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
    const appId = item.runnables.APP.appId;
    const appInfoRes = await getAppInfo(tenantId, appId);
    if (appInfoRes.code === 0) {
      dispatch(setAtAppInfo(appInfoRes.data));
    }
    dispatch(setAtAppId(appId));
    setAppName(item.name);
    setShowAt(false);
    dispatch(setOpenStar(false));
    if (appId !== atAppId) {
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
    if (chatRunning) {
      Message({ type: 'warning', content: t('tryLater') })
      return;
    }
    dispatch(setChatRunning(false));
    updateChatId(null, appId, dimension);
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
      Message({ type: 'warning', content: t('tryLater') });
      return true;
    }
    return false;
  }
  // 点击更多应用按钮回调
  const onClickShowMore = () => {
    if (chatType !== 'active') return;
    showMoreClick();
  }
  // 公告
  const announcementsClick = () => {
    let { publishedUpdateLog } = appInfo.attributes;
    if (publishedUpdateLog && publishedUpdateLog.length) {
      setNotice(publishedUpdateLog);
    } else {
      setNotice(t('noAnnouncement'));
    }
    setShowNotice(true);
  }
  return (
    <div className='btn-inner'>
      <div className='inner-left'>
        <div className='inner-item'>
          {appIcon ? <img src={appIcon} alt='' /> : <img src={knowledgeBase} alt='' />}
          <div className={['switch-app', atAppId ? 'switch-active' : null].join(' ')} onClick={onClickShowMore}>
            {atAppId && <span style={{ marginLeft: '6px' }}>{t('chatWith')}</span>}
            <span className='item-name' title={appName}>{appName}</span>
            {!appInfo.hideHistory && <img src='./src/assets/images/ai/list.png' className='app-menu' />}
            {atAppId && <span style={{ marginLeft: '6px' }}>{t('chat')}</span>}
          </div>
          <LinkIcon onClick={uploadClick} />
          {(!atAppId && appId === HOME_APP_ID) && <AtIcon onClick={atClick} />}
        </div>
      </div>
      <div className='inner-right'>
        {
          atAppId ?
            (
              <div className='inner-item'>
                <CloseOutlined className='item-close' onClick={cancelAt} />
              </div>
            ) :
            (
              <div className='inner-item'>
                <ShareIcon onClick={() => setEditorShow(true, 'share')} />
                <NotificationIcon onClick={announcementsClick} />
                { !appInfo.hideHistory && <HistoryIcon onClick={historyChatClick} />}
                {showMulti && <div className='multi-conversation-title'>
                  <span>{t('multiTurnConversation')}</span>
                  <Switch className='multi-conversation-switch' checked={useMemory} onChange={onMultiConverChange} />
                </div>}
                <span className='item-clear' onClick={onClickNewChat}>+ {t('newChat')}</span>
              </div>
            )
        }
      </div>
      { showAt && <ReferencingApp atItemClick={atItemClick}
        atClick={showMoreClick}
        searchKey={searchKey}
        setSearchKey={setSearchKey} />}
      {/* 清空历史记录 */}
      <Modal
        title={t('clearCurrentChat')}
        open={isModalOpen}
        onOk={handleOk}
        onCancel={() => setIsModalOpen(false)}
        centered>
        <span>{t('clearCurrentChatContent')}</span>
      </Modal>
      {/* 公告 */}
      <Modal
        title={t('updateLog')}
        width={800}
        open={showNotice}
        onCancel={() => setShowNotice(false)}
        footer={null}>
        <div style={{ maxHeight: '400px', overflow: 'auto' }}>
          <div dangerouslySetInnerHTML={{ __html: notice }}></div>
        </div>
        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <Button onClick={() => setShowNotice(false)}>{t('gotIt')}</Button>
        </div>
      </Modal>
      <StarApps
        handleAt={atItemClick}
      />
      <UploadFile openUploadRef={openUploadRef} fileSend={fileSend} />
      <HistoryChatDrawer openHistorySignal={openHistorySignal} />
    </div>
  );
}


export default EditorBtnHome;
