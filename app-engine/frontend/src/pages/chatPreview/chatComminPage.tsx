
import React, { useEffect } from 'react';
import { AippContext } from '../aippIndex/context';
import ChatPreview from './index';
import { useAppDispatch } from '../../store/hook';

// 公共参数，公共聊天界面
const CommonChat = ({ chatType, contextProvider, previewBack}) => {
  return (
    <AippContext.Provider value={{...contextProvider}}> 
      <ChatPreview previewBack={previewBack} chatType={chatType} />
    </AippContext.Provider>
  )
};

export default CommonChat;
