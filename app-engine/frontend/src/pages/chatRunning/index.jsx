
import React, { useEffect, useState, useRef } from 'react';
import { Button } from 'antd';
import { useParams } from 'react-router-dom';
import { AippContext } from '../aippIndex/context';
import { getAippInfo } from '../../shared/http/aipp';
import { useNavigate } from 'react-router-dom';
import './index.scss';
import CommonChat from '../chatPreview/chatComminPage';

const ChatRunning = () => {
  const { appId, tenantId } = useParams();
  const [ aippInfo, setAippInfo ] = useState({});
  const navigate = useNavigate();
  useEffect(() => {
    getAippDetails();
  }, []);

  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAippInfo(tenantId, appId);
    if (res.code === 0) {
      setAippInfo(() => {
        res.data.notShowHistory = true;
        return res.data
      });
    }
  }

  const contextProvider = {
    appId,
    tenantId,
    aippInfo
  }; 
  return (
    <div className="chat-running-container">
      <div className="chat-running-chat"><Button type='text' onClick={()=> {
        navigate(-1)
      }}>返回</Button>{ aippInfo.name }</div>
      <CommonChat contextProvider={contextProvider}/> 
    </div>
  )
};


export default ChatRunning;
