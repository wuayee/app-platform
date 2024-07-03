
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context.js';
import { queryFeedback } from '@shared/http/chat';
import { deepClone, scrollBottom } from '../utils/chat-process';
import '../styles/chat-message-style.scss';
import { useAppDispatch, useAppSelector } from '../../../store/hook';
import { setChatList } from '../../../store/chatStore/chatStore';
import { Message } from '@shared/utils/message';

const ChatMessaga = (props) => {
  const dispatch = useAppDispatch();
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const dataDimension = useAppSelector((state) => state.commonStore.dimension);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const [ list, setList ] = useState([]);
  const { showCheck, setCheckedList, setEditorShow, feedRef, chatRunningStop } = props;
  const initFeedbackStatus = async (id) => {
    let arr = JSON.parse(JSON.stringify(chatList))
    for (let i = 0; i < arr?.length; i++) {
      let item = arr[i]
      if (item.type === 'recieve' && item?.instanceId && (id === 'all' || item?.instanceId === id)) {
        await queryFeedback(item.instanceId).then((res) => {
          if (!res) {
            item.feedbackStatus = -1 ;
          } else {
            item.feedbackStatus = res.usrFeedback ;
          }
        });
      }
    }
    dispatch(setChatList(arr));
  }
  useImperativeHandle(feedRef, () => {
    return {
      'initFeedbackStatus': initFeedbackStatus,
      'setCheckStatus': setCheckStatus
    }
  })
  useEffect(() => {
    setList(deepClone(chatList));
    scrollBottom();
  }, [chatList]);

  // 重置选中状态
  const setCheckStatus = () => {
    list.forEach(item => item.checked = false);
  }
  // 分享问答
  function setShareClass() {
    setCheckStatus();
    setEditorShow(true);
  }
  
  // 选中回调
  function checkCallBack() {
    let checkList = list?.filter(item => item.checked);
    setCheckedList(checkList);
  }

  // 澄清表单拒绝澄清回调
  async function handleRejectClar(instanceId) {
    const params = {content: '不好意思，请明确条件后重新提问'};
    const res = await chatRunningStop(params);
    if (res === 0) {
      Message({ type: "success", content: "已终止对话" });
      const item = {
        content: '不好意思，请明确条件后重新提问',
        type: 'recieve',
        loading: false,
        openLoading: false,
        checked: false,
        logId:  -1,
        markdownSyntax: false,
        instanceId,
        feedbackStatus: -1,
      }
      chatList.pop();
      dispatch(setChatList([...chatList, item]));
    } else {
      Message({ type: "error", content: "终止对话失败" });
    }
  }
  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      { !list?.length && <ChatDetail /> }
      <ChatContext.Provider
        value={{ checkCallBack, setShareClass, showCheck, handleRejectClar, useMemory, dataDimension, tenantId}}>
        <div className='message-box'>
          {
            list?.map((item, index) => {
              return (
                item.type === 'send' ?
                <SendBox chatItem={item} key={index} /> :
                <ReciveBox chatItem={item} key={index} refreshFeedbackStatus={initFeedbackStatus} />
              )
            })
          }
        </div>
      </ChatContext.Provider>
    </div>
  )
};

export default ChatMessaga;
