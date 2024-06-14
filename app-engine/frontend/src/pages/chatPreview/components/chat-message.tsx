
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context.js';
import { queryFeedback } from '@shared/http/chat';
import '../styles/chat-message-style.scss';
import { useAppDispatch, useAppSelector } from '../../../store/hook';
import { setChatList } from '../../../store/chatStore/chatStore';

const ChatMessaga = (props) => {
  const dispatch = useAppDispatch();
  const chatListConstant = useAppSelector((state) => state.chatCommonStore.chatList);
  const { showCheck, setCheckedList, setEditorShow, feedRef } = props;
  let chatList=JSON.parse(JSON.stringify(chatListConstant));
  const initFeedbackStatus = async (id) => {
    let arr = chatList;
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
      'initFeedbackStatus': initFeedbackStatus
    }
  })
  useEffect(() => {
    scrollBottom();
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
  function checkCallBack(index,check) {
    dispatch(setChatList(chatList));
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
                  <SendBox chatItem={item} key={index} index={index}/> :
                  <ReciveBox chatItem={item} key={index} index={index} refreshFeedbackStatus={initFeedbackStatus} />
              )
            })
          }
        </div>
      </ChatContext.Provider>
    </div>
  )
};

export default ChatMessaga;
