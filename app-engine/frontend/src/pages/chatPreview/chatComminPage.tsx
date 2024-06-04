
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
  const[clearChat,setClearChat] =useState(null);
  const [ openStar, setOpenStar ] = useState(false);
  const [inspirationOpen,setInspirationOpen] =useState(false);
  useEffect(() => {
    getUser();
  }, []);

  // 获取用户信息
  const getUser = () => {
    getCurUser().then(res => {
      localStorage.setItem('currentUserId', res.data.account?.substr(1));
      localStorage.setItem('currentUserIdComplete', res.data.account);
      localStorage.setItem('currentUser', res.data.chineseName);
    })
  }

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
    ...contextProvider
  }; 
  return (
    <AippContext.Provider value={provider}> 
      <ChatPreview chatType={chatType} previewBack={previewBack}/>
    </AippContext.Provider>
  )
};


export default CommonChat;
