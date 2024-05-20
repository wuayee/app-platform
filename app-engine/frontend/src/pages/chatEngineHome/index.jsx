
import React, { useEffect, useState, useRef } from 'react';
import { AippContext } from '../aippIndex/context';
import { getCurUser, getAippInfo } from '../../shared/http/aipp';
import ChatPreview from '__pages/chatPreview/index.jsx';
import './index.scss'

const ChatRunning = () => {
  const appId = '3a617d8aeb1d41a9ad7453f2f0f70d61';
  const tenantId = '727d7157b3d24209aefd59eb7d1c49ff';
  const [ aippInfo, setAippInfo ] = useState({});
  const [ prompValue, setPrompValue ] = useState({});
  const [ chatRunning, setChatRunning ] = useState(false);
  const [ refreshPrompValue, setRefreshPrompValue ] = useState(false);
  const aippRef = useRef(null);
  useEffect(() => {
    getUser();
    getAippDetails();
  }, []);

  // 获取用户信息
  const getUser = () => {
    getCurUser().then(res => {
      localStorage.setItem('currentUserId', res.data.account?.substr(1));
      localStorage.setItem('currentUser', res.data.chineseName);
    })
  }
  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAippInfo(tenantId, appId);
    if (res.code === 0) {
      setAippInfo(() => {
        aippRef.current = JSON.parse(JSON.stringify(res.data));
        return res.data
      });
    }
  }
  // 设置会话状态
  const chatStatusChange = (running) => {
    setChatRunning(running)
  }
  const provider = {
    appId,
    tenantId,
    aippInfo,
    chatRunning,
    prompValue,
    setPrompValue,
    refreshPrompValue,
    setRefreshPrompValue,
  };
  return (
    <>
      {
        <div className="chat-engine-container">
           <AippContext.Provider value={provider}>
              <ChatPreview chatStatusChange={chatStatusChange}/>
           </AippContext.Provider>
        </div>
      }
    </>
  )
};


export default ChatRunning;
