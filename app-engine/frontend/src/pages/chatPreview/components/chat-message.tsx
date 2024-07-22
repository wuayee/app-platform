
import React, { useEffect, useState, useImperativeHandle } from 'react';
import SendBox from './send-box/send-box';
import ReciveBox from './recieve-box/recieve-box';
import ChatDetail from './chat-details';
import { ChatContext } from '../../aippIndex/context';
import { queryFeedback } from '@shared/http/chat';
import { deepClone, scrollBottom } from '../utils/chat-process';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setChatList } from '@/store/chatStore/chatStore';
import '../styles/chat-message-style.scss';

const ChatMessaga = (props) => {
  const dispatch = useAppDispatch();
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const dataDimension = useAppSelector((state) => state.commonStore.dimension);
  const [ list, setList ] = useState([]);
  const { 
    showCheck, 
    setCheckedList,
    setEditorShow, 
    feedRef, 
    chatRunningStop, 
    conditionConfirm 
  } = props;
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
  async function handleRejectClar() {
    const params = {content: '不好意思，请明确条件后重新提问'};
    chatRunningStop(params);
  }
  return (
    <div className={['chat-message-container', showCheck ? 'group-active' : null].join(' ')} id='chat-list-dom'>
      { !list?.length && <ChatDetail /> }
      <ChatContext.Provider
        value={{ checkCallBack, setShareClass, showCheck, handleRejectClar, useMemory, dataDimension, conditionConfirm, tenantId}}>
        <div className='message-box'>
          {
            list?.map((item, index) => {
              return (
                item.type === 'send' ?
                <SendBox chatItem={item} key={index} /> :
                <ReciveBox 
                  chatItem={item} 
                  key={index} 
                  refreshFeedbackStatus={initFeedbackStatus}
                />
              )
            })
          }
        </div>
      </ChatContext.Provider>
    </div>
  )
};

export default ChatMessaga;
