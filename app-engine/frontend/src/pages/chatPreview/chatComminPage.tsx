
import React, { useEffect, useState } from 'react';
import { Button } from 'antd';
import { useParams } from 'react-router-dom';
import { AippContext } from '../aippIndex/context';
import { getCurUser } from '../../shared/http/aipp';
import ChatPreview from '.';

// 公共参数，公共聊天界面
const CommonChat = ({chatType,contextProvider,previewBack}) => {
  const [ chatRunning, setChatRunning ] = useState(false);
  const [chatId,setChatId]=useState(null);
  const [chatList, setChatList] = useState([]);
  const [clearChat,setClearChat] =useState(null);
  const [ openStar, setOpenStar ] = useState(false);
  const [inspirationOpen,setInspirationOpen] =useState(false);
  
  const provider = {
    chatRunning,
    setChatRunning,
    chatList,
    setChatList,
    chatId,
    setChatId,
    clearChat,
    setClearChat,
    inspirationOpen,
    setInspirationOpen,
    openStar,
    setOpenStar,
    ...contextProvider,
    chatType
  }; 

  return (
    <AippContext.Provider value={provider}> 
      <ChatPreview chatType={chatType} previewBack={previewBack}/>
    </AippContext.Provider>
  )
};


export default CommonChat;
