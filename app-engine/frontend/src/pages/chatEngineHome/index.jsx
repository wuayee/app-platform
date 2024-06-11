
import React, { useEffect, useState, useRef } from 'react';
import { getCurUser, getAppInfo, clearInstance } from '../../shared/http/aipp';
import { useAppDispatch, useAppSelector } from '../../store/hook';
import { useBeforeUnload, useLocation } from "react-router-dom";
import './index.scss'
import {getUserCollection} from '../../shared/http/appDev'
import { setCollectionValue, setCurAppId } from "../../store/collection/collection";
import CommonChat from '../chatPreview/chatComminPage';
import { getUser } from '../helper';
import { setAppId, setAppInfo } from '../../store/appInfo/appInfo';

const xiaohaiAppId='3a617d8aeb1d41a9ad7453f2f0f70d61';
const ChatRunning = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();
  const curAppId = useAppSelector((state) => state.collectionStore.AppId);
  const appId = useAppSelector((state) => state.appStore.AppId);
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  
  useEffect(()=>{
    if(curAppId){
    getAppDetails();
    dispatch(setAppId(curAppId));
    }
  },[curAppId]);
  
  useEffect(()=>{
    initialApp();
  },[]);


  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserIdComplete') ?? '';
  }

  // 第一次加载界面，获取user信息
  const initialApp = async () => {
    //获取appId
    await getUser();
    const collectionInfo = await getUserCollection(getLoaclUser());
    const defaultData = collectionInfo?.data?.defaultApp || null;
    if(!appId) {
      dispatch(setCurAppId(defaultData?.appId || ''))
      dispatch(setAppId(defaultData?.appId));
    }
  }

  const getAppDetails=async ()=>{
    // 设置当前应用
    const res = await getAppInfo(tenantId, curAppId || xiaohaiAppId);
    if (res.code === 0) {
      res.data.notShowHistory = true;
      dispatch(setAppInfo(res.data));
    }
  }

  useEffect(() => {
    // 清除默认应用
    dispatch(setCurAppId(''));
  }, [location]);

  return (
    <div className="chat-engine-container">
      <CommonChat chatType='home'/> 
    </div>
);
  }


export default ChatRunning;
