
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context.js';
import { queryFeedback } from '@shared/http/chat';
import '../styles/chat-message-style.scss';
import { useAppDispatch, useAppSelector } from '../../../store/hook';
import { setChatList } from '../../../store/chatStore/chatStore.js';

const ChatMessaga = (props) => {
  const dispatch = useAppDispatch();
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const { showCheck, setCheckedList, setEditorShow } = props;
  const initFeedbackStatus = async (id) => {
    for (let i = 0; i < chatList?.length; i++) {
      let item = chatList[i]
      if (item.type === 'recieve' && item?.instanceId && (id === 'all' || item?.instanceId === id)) {
        await queryFeedback(item.instanceId).then((res) => {
          if (!res) {
            item.feedbackStatus = -1;
          } else {
            item.feedbackStatus = res.usrFeedback;
          }
        });
      }
    }
    dispatch(setChatList([...chatList]));
  }

  useEffect(() => {
    scrollBottom();
    initFeedbackStatus('all');

  }, [chatList?.length]);

  const scrollBottom = () => {
    setTimeout(() => {
      const messageBox = document.getElementById('chat-list-dom');
      messageBox?.scrollTo({
        top: messageBox.scrollHeight,
        behavior: 'smooth',
      });
    }, 100)
  }
  // 分享问答
  function setShareClass() {
    setEditorShow(true);
  }
  
  // 选中回调
  function checkCallBack() {
    let checkList = chatList?.filter(item => item.checked);
    setCheckedList(checkList);
  }
  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      { !chatList?.length && <ChatDetail /> }
      <ChatContext.Provider value={{ checkCallBack, setShareClass, showCheck}}>
        <div className='message-box'>
          {
            chatList?.map((item, index) => {
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
