
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import SendBox from './send-box/send-box.jsx';
import ReciveBox from './recieve-box/recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { AippContext, ChatContext } from '../../aippIndex/context';
import { queryFeedback } from '@shared/http/chat';
import '../styles/chat-message-style.scss';

const ChatMessaga = (props) => {
  const {chatList,setChatList} = useContext(AippContext);
  const { showCheck, setCheckedList } = props;
  const initFeedbackStatus = async (id) => {
    if (id === 'all') {
      for (let i = 0; i < chatList.length; i++) {
        let item = chatList[i]
        if (item.type === 'recieve' && item.instanceId) {
          await queryFeedback(item.instanceId).then((res) => {
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
        if (item.type === 'recieve' && item.instanceId && item.instanceId === id) {
          await queryFeedback(item.instanceId).then((res) => {
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
    initFeedbackStatus('all');
  }, [chatList?.length])
  
  const scrollBottom = () => {
    setTimeout(() => {
      const messageBox = document.getElementById('chat-list-dom');
      messageBox?.scrollTo({
        top: messageBox.scrollHeight,
        behavior: 'smooth',
      });
    }, 100)
  }
  // 选中回调
  function checkCallBack() {
    let checkList = chatList?.filter(item => item.checked);
    setCheckedList(checkList);
  }
  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      { !chatList?.length && <ChatDetail /> }
      <ChatContext.Provider value={{ checkCallBack, showCheck}}>
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
