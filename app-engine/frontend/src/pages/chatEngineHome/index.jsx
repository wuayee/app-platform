
import React, { useEffect, useState, useRef } from 'react';
import { getCurUser, getAippInfo, clearInstance } from '../../shared/http/aipp';
import { useAppDispatch, useAppSelector } from '../../store/hook';
import { useBeforeUnload, useLocation } from "react-router-dom";
import './index.scss'
import {getUserCollection} from '../../shared/http/appDev'
import { setCollectionValue, setDefaultApp } from "../../store/collection/collection";
import CommonChat from '../chatPreview/chatComminPage';

const ChatRunning = () => {
  const [appId,setAppId] = useState('3a617d8aeb1d41a9ad7453f2f0f70d61');
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const [ aippInfo, setAippInfo ] = useState({});
  const location = useLocation();

  const contextProvider={
    appId: aippId ?? appId,
    aippInfo,
    tenantId
  };

  const aippId = useAppSelector((state) => state.collectionStore.defaultAppId);
  
  useEffect(()=>{
    getAippDetails();
  },[appId])

  useEffect(()=>{
    let appIdStr=aippId;
    if(appIdStr){
      setAppId(aippId);
    }
  },[aippId]);

  const dispatch = useAppDispatch();

  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  // 获取aipp详情
  const getAippDetails = async () => {

    if(!localStorage.getItem('currentUserIdComplete')) {
      await getUser();
    }
    const collectionInfo = await getUserCollection(getLoaclUser());
    const defaultData = collectionInfo?.data?.defaultApp || null;

    if(!aippId) {
      dispatch(setDefaultApp(defaultData?.appId || ''))
    }

    // 设置默认应用
    // 获取默认收藏
    const res = await getAippInfo(tenantId, (aippId || appId));
    if (res.code === 0) {
      setAippInfo(() => {
        res.data.notShowHistory = true;
        return res.data
      });
    }
  }

  useEffect(() => {
    // 清除默认应用
    dispatch(setDefaultApp(''))
  }, [location]);

  return (
    <div className="chat-engine-container">
      <CommonChat chatType='home' contextProvider={contextProvider}/> 
    </div>
);
  }


export default ChatRunning;
