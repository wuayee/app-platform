
import React, { useEffect, useState, useRef } from 'react';
import { AippContext } from '../aippIndex/context';
import { getCurUser, getAippInfo, clearInstance } from '../../shared/http/aipp';
import ChatPreview from '__pages/chatPreview/index.jsx';
import { useAppDispatch, useAppSelector } from '../../store/hook';
import { useBeforeUnload, useLocation, useNavigate } from "react-router-dom";
import './index.scss'
import {getUserCollection} from '../../shared/http/appDev'
import { setCollectionValue, setDefaultApp } from "../../store/collection/collection";

const ChatRunning = () => {
  const [appId,setAppId] = useState('3a617d8aeb1d41a9ad7453f2f0f70d61');
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const [ aippInfo, setAippInfo ] = useState({});
  const listRef = useRef(null);
  const [ prompValue, setPrompValue ] = useState({});
  const [ chatRunning, setChatRunning ] = useState(false);
  const [chatList, setChatList] = useState([]);
  const [chatId,setChatId]=useState(null);
  const [ refreshPrompValue, setRefreshPrompValue ] = useState(false);
  const [requestLoading, setRequestLoading] = useState(false);
  let timerRef = useRef(null);
  const aippRef = useRef(null);
  const[clearChat,setClearChat] =useState(null);

  const navigate = useNavigate();

  const provider={
    appId: aippId ?? appId,
    tenantId,
    aippInfo,
    chatRunning,
    prompValue,
    setPrompValue,
    refreshPrompValue,
    setRefreshPrompValue,
    showHistory: true,
    setChatRunning,
    chatList,
    setChatList,
    chatId,
    setChatId,
    timerRef,
    requestLoading, 
    setRequestLoading,
    clearChat,
    listRef,
    setClearChat
  };

  const aippId = useAppSelector((state) => state.collectionStore.defaultAppId);
  useEffect(()=>{
    setAppId(aippId);
    getAippDetails();
  },[aippId]);

  // 获取用户信息
  const getUser = async () => {
    let res = await getCurUser();
    localStorage.setItem('currentUserId', res.data.account?.substr(1));
    localStorage.setItem('currentUserIdComplete', res.data.account);
    localStorage.setItem('currentUser', res.data.chineseName);
  }

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
        aippRef.current = JSON.parse(JSON.stringify(res.data));
        return res.data
      });
    }
  }

  // 设置会话状态
  const chatStatusChange = (running) => {
    setChatRunning(running)
  }


  const location = useLocation();

  useEffect(() => {
    // 清除默认应用
    dispatch(setDefaultApp(''))

  }, [location]);

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
