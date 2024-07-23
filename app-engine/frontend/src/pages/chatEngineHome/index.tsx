
import React, { useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import CommonChat from '../chatPreview/chatComminPage';
import InfoModal from './components/InfoModal';
import { getAppInfo } from '@/shared/http/aipp';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import {getUserCollection} from '@/shared/http/appDev'
import { setCurAppId } from '@/store/collection/collection';
import { setHistorySwitch } from '@/store/common/common';
import { getUser } from '../helper';
import { setAppId, setAppInfo } from '@/store/appInfo/appInfo';
import './index.scss'

const xiaohaiAppId='3a617d8aeb1d41a9ad7453f2f0f70d61';
const ChatRunning = () => {
  const location = useLocation();
  const dispatch = useAppDispatch();
  const curAppId = useAppSelector((state) => state.collectionStore.AppId);
  const appId = useAppSelector((state) => state.appStore.AppId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  
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
      const memory = res.data.config.form.properties.filter(item => item.name === 'memory')[0];
      dispatch(setHistorySwitch(memory.defaultValue.type !==  'NotUseMemory'));
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
  },[]);
  return (
    <div className='chat-engine-container'>
      <CommonChat chatType='active'/>
      <InfoModal />
    </div>
);
  }


export default ChatRunning;
