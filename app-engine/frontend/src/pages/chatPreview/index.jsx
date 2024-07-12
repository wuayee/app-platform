import React, { useEffect, useState, useContext, useRef } from 'react';
import { useLocation } from 'react-router';
import { Spin } from 'antd';
import { LeftArrowIcon } from '@assets/icon';
import { Message } from '@shared/utils/message';
import { isJsonString } from '@shared/utils/common';
import ChatMessage from './components/chat-message';
import SendEditor from './components/send-editor/send-editor.jsx';
import CheckGroup from './components/check-group.jsx';
import Inspiration from './components/inspiration.jsx';
import { initChat} from './common/config';
import { AippContext } from '../aippIndex/context';
import {
  aippDebug,
  updateFlowInfo,
  getAppRecentlog,
  stopInstance,
  getReportInstance
} from '@shared/http/aipp';
import { 
  historyChatProcess, 
  inspirationProcess,
  reportProcess,
  messageProcess,
  messageProcessNormal,
  beforeSend,
  deepClone,
  scrollBottom } from './utils/chat-process';
import "./styles/chat-preview.scss";
import { creatChat, updateChat } from "@shared/http/chat.js";
import { useAppDispatch, useAppSelector } from "@/store/hook";
import {
  setAtChatId,
  setChatId,
  setChatList,
  setChatRunning,
  setInspirationOpen,
  setFormReceived
} from "@/store/chatStore/chatStore";
import { v4 as uuidv4 } from 'uuid';

const ChatPreview = (props) => {
  const { previewBack } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
  const chatType = useAppSelector((state) => state.chatCommonStore.chatType);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const atChatId = useAppSelector((state) => state.chatCommonStore.atChatId);
  const atAppId = useAppSelector((state) => state.appStore.atAppId);
  const atAppInfo = useAppSelector((state) => state.appStore.atAppInfo);
  const dimension = useAppSelector((state) => state.commonStore.dimension);
  const formReceived = useAppSelector((state) => state.chatCommonStore.formReceived);
  const useMemory = useAppSelector((state) => state.commonStore.useMemory);
  const { showElsa } = useContext(AippContext);
  const [checkedList, setCheckedList] = useState([]);
  const [isScroll, setIsScroll] = useState(true);
  const [loading, setLoading] = useState(false);
  const [groupType, setGroupType] = useState('share');
  const [showCheck, setShowCheck] = useState(false);
  const location = useLocation();
  let editorRef = React.createRef();
  let runningInstanceId = useRef('');
  let currentInfo = useRef();
  let feedRef = useRef();
  let testRef = useRef(false);
  let conditionChatRef = useRef();
  let runningVersion = useRef("");
  let runningAppid = useRef("");
  let childInstanceStop = useRef(false);
  let wsCurrent = useRef(null);
  let reportInstance = useRef('');
  let reportIContext = useRef(null);
  const listRef = useRef([]);
  const detailPage =  location.pathname.indexOf('app-detail') !== -1;
  useEffect(() => {
    !chatType && dispatch(setInspirationOpen(true));
    currentInfo.current = appInfo;
  }, []);

  useEffect(() => {
    testRef.current = formReceived;
  }, [formReceived])

  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem) {
    let { prompt, promptArr } = inspirationProcess(tenantId, data, prompItem);
    editorRef.current.setFilterHtml(prompt, promptArr);
  }
  // 获取历史会话
  async function initChatHistory() {
    listRef.current = [];
    setLoading(true);
    try {
      const res = await getAppRecentlog(tenantId, appId, 'preview');
      if (res.data && res.data.length) {
        let chatArr = historyChatProcess(res);
        listRef.current = deepClone(chatArr);
        await dispatch(setChatList(chatArr));
        feedRef.current.initFeedbackStatus('all');
      }
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    if (!currentInfo.current || currentInfo.current.id !== appInfo.id) {
      dispatch(setChatRunning(false));
      dispatch(setChatId(null));
      dispatch(setChatList([]));
      (appInfo.name && !appInfo.notShowHistory) && initChatHistory();
    }
  }, [appInfo.id]);
  
  // 发送消息
  const onSend = (value, type = undefined) => {
    const sentItem = beforeSend(chatRunning, value, type);
    if (sentItem) {
      let arr = [...chatList, sentItem];
      listRef.current = arr;
      sendMessageRequest(value, type);
    }
  };
  // 发送消息
  const sendMessageRequest = async (value, type) => {
    const reciveInitObj = deepClone(initChat);
    reciveInitObj.type = 'recieve';
    reciveInitObj.loading = true;
    if (atAppInfo) {
      reciveInitObj.appName = atAppInfo.name;
      reciveInitObj.appIcon = atAppInfo.attributes.icon;
      reciveInitObj.isAt = true;
    }
    let arr = [...listRef.current, reciveInitObj];
    listRef.current = deepClone(arr);
    dispatch(setChatList(deepClone(arr)));
    dispatch(setChatRunning(true));
    setTimeout(() => {
      scrollBottom();
    }, 50);
    if (showElsa) {
      let params = appInfo.flowGraph;
      window.agent
        .validate()
        .then(async () => {
          const res = await updateFlowInfo(tenantId, appId, params);
          if (res.code !== 0) {
            onStop('更新grpha数据失败');
          } else {
            getAippAndVersion(value, type);
          }
        })
        .catch((err) => {
          Message({ type: 'warning', content: '请输入必填项' });
          onStop('对话失败');
        });
    } else {
      getAippAndVersion(value, type);
    }
  };
  // 获取aipp_id和version
  async function getAippAndVersion(value, type) {
    let chatAppId = appId;
    let chatAppInfo = appInfo;
    if (atAppId) {
      chatAppInfo = atAppInfo;
      chatAppId = atAppId;
    }
    try {
      const debugRes = await aippDebug(tenantId, chatAppId, chatAppInfo);
      if (debugRes.code === 0) {
        chatMissionStart(debugRes.data, value, type);
      } else {
        onStop(debugRes.msg || '获取aippId失败');
      }
    } catch {
      onStop('获取aippId失败');
    }
  }
  // 启动任务
  const chatMissionStart = async (res, value, type) => {
    let { aipp_id, version } = res;
    let params = type ? 
      { initContext: { '$[FileDescription]$': value, dimension}} : { initContext: { Question: value, dimension } };
    params.initContext.useMemory = useMemory;
    try {
      const requestBody={
        aipp_id:aipp_id,
        aipp_version:version,
        init_context:params,
      }
      if (atAppId) {
        requestBody.origin_app = appInfo.id;
        requestBody.origin_app_version = appInfo.version;
      }
      if (atChatId) {
        requestBody.chat_id = atChatId;
      }
      let res;
      if(chatId){
        res= await updateChat(tenantId, chatId, requestBody);
      } else {
        res= await creatChat(tenantId, requestBody);
        dispatch(setChatId(res?.data?.origin_chat_id));
      }
      childInstanceStop.current = false;
      const instanceId = res?.data?.current_instance_id;
      if (atAppId) {
        dispatch(setAtChatId(res?.data?.chat_id));
      }
      if (instanceId) {
        childInstanceStop.current = false;
        queryInstance(aipp_id, version, instanceId);
      } else {
        onStop('对话失败');
      }
    } catch {
      onStop('对话失败');
    }
  };
  // 开始对话
  const queryInstance = (aipp_id, version, instanceId) => {
    runningInstanceId.current = instanceId;
    runningVersion.current = version;
    runningAppid.current = aipp_id;
    if (!wsCurrent.current) {
      const prefix = window.location.protocol === 'http:' ? 'ws' : 'wss';
      wsCurrent.current = new WebSocket(`${prefix}://${window.location.host}/api/jober/v1/api/aipp/wsStream?aippId=${aipp_id}&version=${version}`);
      wsCurrent.current.onopen = () => {
        wsCurrent.current.send(JSON.stringify({'aippInstanceId': instanceId}));   
      }
    } else {
      wsCurrent.current.send(JSON.stringify({'aippInstanceId': instanceId}));
    }
    wsCurrent.current.onerror = () => {
      onStop('socket对话失败');
      dispatch(setChatRunning(false));
    }
    wsCurrent.current.onmessage = ({ data }) => {
      let messageData = {};
      try {
        messageData = JSON.parse(data);
        // 用户自勾选
        if (messageData.memory === 'UserSelect') {
          selfSelect(messageData.instanceId, messageData.initContext);
          return
        }
        // 智能表单
        if (messageData.formAppearance?.length) {
          let obj = messageProcess(aipp_id, instanceId, version, messageData, atAppInfo);
          chatForm(obj);
          return;
        }
        // 普通日志
        messageData.aippInstanceLogs?.forEach(log => {
          if (log.logData && log.logData.length) {
            let { msg, recieveChatItem } = messageProcessNormal(log, instanceId, atAppInfo);
            if (log.msgId !== null) {
              chatSplicing(log, msg, recieveChatItem, messageData.status);
            } else {
              chatStrInit(msg, recieveChatItem, messageData.status);
            }
          }
        })
        if (['ARCHIVED', 'ERROR'].includes(messageData.status)) {
          dispatch(setChatRunning(false));
          setIsScroll(true);
          
        }
      } catch (err){
        onStop('数据解析异常');
        dispatch(setChatRunning(false));
      }
    }
  }
  // 聊天表单
  const chatForm = (chatObj) => {
    const idx = listRef.current.length - 1;
    listRef.current.splice(idx, 1, chatObj);
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
  }
  // 用户自勾选
  function selfSelect(instanceId, initContext) {
    reportInstance.current = instanceId;
    reportIContext.current = initContext;
    dispatch(setChatList(deepClone(listRef.current)));
    onStop('请勾选对话');
    setCheckedList([]);
    feedRef.current.setCheckStatus();
    setEditorShow(true, 'report');
  }
  // 用户自勾选确定回调
  async function reportClick(list) {
    let memoriesList = reportProcess(list, listRef);
    let params = {
      initContext: {
        Question: listRef.current[listRef.current.length - 2].content,
        ...reportIContext.current.initContext,
        memories: JSON.stringify(memoriesList)
      }
    }
    try {
      const startes = await getReportInstance(tenantId, reportInstance.current, params);
      if (startes.code === 0 && startes.data) {
        let instanceId = startes.data;
        listRef.current[listRef.current.length - 1].loading = true;
        dispatch(setChatList(deepClone(listRef.current)));
        queryInstance(runningAppid.current, runningVersion.current, instanceId);
      } else {
        onStop('启动任务失败');
      }
    } catch {
      onStop('启动任务失败');
    } finally {
      setEditorShow(false);
    }
  }
  // 流式输出
  function chatStrInit(msg, initObj, status) {
    let idx = 0;
    if (isJsonString(msg)) {
      let msgObj = JSON.parse(msg);
      if (msgObj.chartData && msgObj.chartType) {
        initObj.chartConfig = msgObj;
      }
    }
    initObj.loading = false;
    initObj.finished = (status === 'ARCHIVED');
    if (conditionChatRef.current) {
      idx = listRef.current.findIndex((item) => item.logId === conditionChatRef.current);
      conditionChatRef.current = undefined;
    } else {
      idx = listRef.current.length - 1
    }
    if (testRef.current) {
      listRef.current.push(initObj);
      dispatch(setFormReceived(false));
    } else {
      listRef.current.splice(idx, 1, initObj);
    }
    dispatch(setChatList(deepClone(listRef.current)));
  }
  // 流式输出拼接
  function chatSplicing(log, msg, initObj, status) {
    let msgId = conditionChatRef.current ? conditionChatRef.current  : log.msgId;
    let currentChatItem = listRef.current.filter(
      (item) => item.logId === msgId
    )[0];
    if (currentChatItem) {
      let index = listRef.current.findIndex((item) => item.logId === log.msgId);
      let item = listRef.current[index];
      let str = '';
      let { content } = currentChatItem;
      str = content + msg;
      item.content = str;
      if (status === 'ARCHIVED') {
        item.finished = true;
        conditionChatRef.current = undefined;
      }
      dispatch(setChatList(deepClone(listRef.current)));
    } else {
      chatStrInit(msg, initObj, status);
    }
  }
  // 显示问答组
  function setEditorShow(val, type='share') {
    !val && setCheckedList([]);
    setShowCheck(val);
    val && setGroupType(type);
  }
  // 终止对话成功回调
  function onStop(str) {
    let item = null;
    if (conditionChatRef.current) {
      let index = listRef.current.findIndex((item) => item.logId === conditionChatRef.current);
      item = listRef.current[index];
      conditionChatRef.current = undefined;
    } else {
      item = listRef.current[listRef.current.length - 1];
    }
    item.content = str;
    item.loading = false;
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
  }
  // 终止进行中的对话
  async function chatRunningStop(params) {
    let str = params.content ? params.content : "已终止对话";
    const res = await stopInstance(tenantId, runningInstanceId.current, {content: str});
    if (res.code === 0) {
      onStop(str);
      wsCurrent.current?.close();
      wsCurrent.current = null;
      Message({ type: 'success', content: '已终止对话' });
      return res.code;
    } else {
      Message({ type: 'error', content: '终止对话失败' });
    }
  }
  // 溯源表单重新对话
  function conditionConfirm(logId, instanceId) {
    let item = listRef.current.filter((item) => item.logId === logId)[0];
    item.loading = true;
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(true));
    runningInstanceId.current = instanceId;
    conditionChatRef.current = logId;
    setIsScroll(false);
  }

  return (
    <div className={`
        chat-preview 
        ${ showElsa ? 'chat-preview-elsa': ''} 
        ${ detailPage ? 'chat-preview-inner' : '' } 
        ${(showElsa && inspirationOpen) ? 'chat-preview-mr' : ''}`}
    >
      <Spin spinning={loading}>
        <span className='icon-back' onClick={previewBack}>
          { showElsa && <LeftArrowIcon /> }
        </span>
        <div className={ `chat-inner ${ !detailPage ? 'chat-page-inner' : ''}`}>
          <div className={ `chat-inner-left ${ inspirationOpen ? 'chat-left-close' : 'no-border'}` }>
            <ChatMessage
              feedRef={feedRef}
              scroll={isScroll}
              chatRunningStop={chatRunningStop}
              setCheckedList={setCheckedList}
              setEditorShow={setEditorShow}
              conditionConfirm={conditionConfirm}
              showCheck={showCheck}/>
            { showCheck ?
              <CheckGroup
                type={groupType}
                setEditorShow={setEditorShow}
                checkedList={checkedList}
                reportClick={reportClick} />
              : 
              <SendEditor
                onSend={onSend}
                onStop={chatRunningStop}
                filterRef={editorRef}
                inspirationOpen={inspirationOpen} />
            }
          </div>
          <div className={`chat-inner-right ${ inspirationOpen ? 'chat-right-close' : '' }`}>
            <Inspiration inspirationClick={onSend} setEditorSelect={setEditorSelect} />
          </div>
        </div>
      </Spin>
    </div>
  )
};

export default ChatPreview;
