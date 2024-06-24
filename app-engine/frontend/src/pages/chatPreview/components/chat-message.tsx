
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context.js';
import { queryFeedback } from '@shared/http/chat';
import { deepClone } from '../utils/chat-process';
import '../styles/chat-message-style.scss';
import { useAppDispatch, useAppSelector } from '../../../store/hook';
import { setChatList } from '../../../store/chatStore/chatStore';

const ChatMessaga = (props) => {
  const dispatch = useAppDispatch();
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const [ list, setList ] = useState([]);
  const { showCheck, setCheckedList, setEditorShow, feedRef } = props;
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

  
  const scrollBottom = () => {
    const messageBox = document.getElementById('chat-list-dom');
    messageBox?.scrollTo({
      top: messageBox.scrollHeight,
      behavior: 'smooth',
    });
  }
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
  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      { !list?.length && <ChatDetail /> }
      <ChatContext.Provider value={{ checkCallBack, setShareClass, showCheck}}>
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
