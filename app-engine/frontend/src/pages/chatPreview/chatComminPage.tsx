
import React, { useEffect, useState } from 'react';
import { AippContext } from '../aippIndex/context';
import { getCurUser } from '../../shared/http/aipp';
import ChatPreview from '.';
import { setChatType } from '../../store/CommonChat/CommonChat';
import { useAppDispatch } from '../../store/hook';

// 公共参数，公共聊天界面
const CommonChat = ({ chatType, contextProvider, previewBack}) => {
  const dispatch = useAppDispatch();
  useEffect(()=>{
    dispatch(setChatType(chatType));
  })
  return (
    <AippContext.Provider value={{...contextProvider}}> 
      <ChatPreview previewBack={previewBack}/>
    </AippContext.Provider>
  )
};

export default CommonChat;
