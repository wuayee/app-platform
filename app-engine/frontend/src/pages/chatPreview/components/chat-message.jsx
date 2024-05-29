
import React, { useEffect, useState, useImperativeHandle } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context';
import { queryFeedback } from '@shared/http/chat';
import '../styles/chat-message-style.scss';

const ChatMessaga = (props) => {
  const { chatList, setEditorShow, showCheck, setCheckedList, setChatList } = props;
  const initFeedbackStatus = async (id) => {
    if (id === -1) {
      for (let i = 0; i < chatList.length; i++) {
        let item = chatList[i]
        if (item.type === 'recieve' && item.logId) {
          await queryFeedback(item.logId).then((res) => {
            if (!res) {
              item.feedbackStatus = -1;
            } else {
              item.feedbackStatus = res.usrFeedback;
            }
          });
        }
      }
    } else {
      for (let i = 0; i < chatList.length; i++) {
        let item = chatList[i]
        if (item.type === 'recieve' && item.logId && item.logId === id) {
          await queryFeedback(item.logId).then((res) => {
            if (!res) {
              item.feedbackStatus = -1;
            } else {
              item.feedbackStatus = res.usrFeedback;
            }
          });
        }
      }
    }
    setChatList([...chatList]);
  }
  useEffect(() => {
    scrollBottom();
  }, [chatList])
  useEffect(() => {
    initFeedbackStatus(-1);
  }, [chatList.length])
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
  // 添加灵感
  function setInspiration() {
  }
  // 选中回调
  function checkCallBack() {
    let checkList = chatList.filter(item => item.checked);
    setCheckedList(checkList);
  }
  return <>{(
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      {!chatList.length && <ChatDetail />}
      <ChatContext.Provider value={{ setShareClass, setInspiration, checkCallBack, showCheck }}>
        <div className='message-box'>
          {
            chatList.map((item, index) => {
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
  )}</>
};


export default ChatMessaga;
