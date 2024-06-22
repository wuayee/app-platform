
import React, { useEffect, useState, useRef } from 'react';
import { Button } from 'antd';
import { useParams } from 'react-router-dom';
import { getAppInfo } from '../../shared/http/aipp';
import { useNavigate } from 'react-router-dom';
import './index.scss';
import CommonChat from '../chatPreview/chatComminPage';
import { setAppId, setAppInfo } from '../../store/appInfo/appInfo';
import { useAppDispatch, useAppSelector } from '../../store/hook';

const ChatRunning = () => {
  const { appId, tenantId } = useParams();
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const navigate = useNavigate();
  useEffect(() => {
    dispatch(setAppId(appId));
    getAippDetails();
  }, []);

  // 获取aipp详情
  const getAippDetails = async () => {
    const res = await getAppInfo(tenantId, appId);
    if (res.code === 0) {
      res.data.notShowHistory = true;
      dispatch(setAppInfo(res.data));
    }
  }

  return (
    <div className="chat-running-container">
      <div className="chat-running-chat"><Button type='text' onClick={()=> {
        navigate(-1)
      }}>返回</Button>{ appInfo.name }</div>
      <CommonChat/> 
    </div>
  )
};


export default ChatRunning;
