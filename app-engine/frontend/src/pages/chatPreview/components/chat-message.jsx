
import React, { useEffect, useState, useImperativeHandle } from 'react';
import SendBox from './send-box.jsx';
import ReciveBox from './recieve-box.jsx';
import ChatDetail from './chat-details.jsx';
import { ChatContext } from '../../aippIndex/context';
import '../styles/chat-message-style.scss';

const ChatMessaga = (props) => {
  const { chatList, setEditorShow, showCheck, setCheckedList } = props;
  useEffect(() => {
    scrollBottom();
  }, [chatList])
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
    console.log(3333333);
  }
  // 选中回调
  function checkCallBack() {
    let checkList = chatList.filter(item => item.checked);
    setCheckedList(checkList);
  }
  return <>{(
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id="chat-list-dom">
      { !chatList.length && <ChatDetail /> }
      <ChatContext.Provider value={{ setShareClass, setInspiration, checkCallBack }}>
        <div className='message-box'>
          {
            chatList.map((item, index) => {
              return (
                item.type === 'send' ?  
                <SendBox chatItem={item} key={index} showCheck={showCheck}/> : 
                <ReciveBox chatItem={item} key={index}/>
              )
            })
          }
        </div>
      </ChatContext.Provider>
    </div>
  )}</>
};


export default ChatMessaga;
