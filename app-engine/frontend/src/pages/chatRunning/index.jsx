
import React, { useEffect, useState, useRef } from 'react';
import { Button } from 'antd';

import { useParams } from 'react-router-dom';
import { AippContext } from '../aippIndex/context';
import { getCurUser, getAippInfo } from '../../shared/http/aipp';
import { HashRouter, Route, useNavigate, Routes } from 'react-router-dom';
import ChatPreview from '__pages/chatPreview/index.jsx';
import './index.scss';

const ChatRunning = () => {
  const { appId, tenantId } = useParams();
  const [ aippInfo, setAippInfo ] = useState({});
  const [ prompValue, setPrompValue ] = useState({});
  const [ chatRunning, setChatRunning ] = useState(false);
  const [ refreshPrompValue, setRefreshPrompValue ] = useState(false);
  const navigate = useNavigate();
  const aippRef = useRef(null);
  useEffect(() => {
    getUser();
    getAippDetails();
  }, []);

  // 获取用户信息
  const getUser = () => {
    getCurUser().then(res => {
      localStorage.setItem('currentUserId', res.data.account?.substr(1));
      localStorage.setItem('currentUserIdComplete', res.data.account);
      localStorage.setItem('currentUser', res.data.chineseName);
    })
  }
  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAippInfo(tenantId, appId);
    if (res.code === 0) {
      setAippInfo(() => {
        res.data.notShowHistory = true;
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
    showHistory: false,
  };
  return (
    <>
      {
        <div className="chat-running-container">
        
          <div className="chat-running-chat"><Button type='text' onClick={()=> {
            navigate(-1)
          }}>返回</Button>{ aippInfo.name }</div>
           <AippContext.Provider value={provider}>
              <ChatPreview chatStatusChange={chatStatusChange}/>
           </AippContext.Provider>
        </div>
      }
    </>
  )
};


export default ChatRunning;
