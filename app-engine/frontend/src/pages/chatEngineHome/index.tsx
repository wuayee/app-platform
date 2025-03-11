/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

import React, { useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import CommonChat from '../chatPreview/chatComminPage';
import InfoModal from './components/InfoModal';
import { getAppInfo } from '@/shared/http/aipp';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { getUserCollection } from '@/shared/http/appDev'
import { setCurAppId } from '@/store/collection/collection';
import { setHistorySwitch, setIsDebug } from '@/store/common/common';
import { getUser } from '../helper';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import { HOME_APP_ID } from '../chatPreview/components/send-editor/common/config';
import { findConfigValue } from '@/shared/utils/common';
import './index.scss'

const ChatRunning = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();
  const curAppId = useAppSelector((state) => state.collectionStore.AppId);
  const appId = useAppSelector((state) => state.appStore.AppId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  
  // 获取当前登录用户名
  const getLoaclUser = () => {
    return localStorage.getItem('currentUserId') ?? '';
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

  const getAppDetails= async ()=>{
    // 设置当前应用
    const res = await getAppInfo(tenantId, curAppId || HOME_APP_ID);
    if (res.code === 0) {
      const appInfo = res.data;
      appInfo.notShowHistory = true;
      dispatch(setAppInfo(appInfo));
      const memoryItem = findConfigValue(appInfo, 'memory');
      dispatch(setHistorySwitch(memoryItem?.type !==  'NotUseMemory'));
    }
  }

  useEffect(() => {
    // 清除默认应用
    dispatch(setCurAppId(''));
  }, [location]);
  
  useEffect(()=>{
    if(curAppId){
      getAppDetails();
      dispatch(setAppId(curAppId));
    }
  },[curAppId]);
  
  useEffect(()=>{
    initialApp();
    dispatch(setIsDebug(false));
  },[]);
  return (
    <div className='chat-engine-container'>
      <CommonChat />
      <InfoModal />
    </div>
);
  }


export default ChatRunning;
