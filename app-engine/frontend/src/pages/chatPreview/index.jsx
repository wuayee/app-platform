import React, { useEffect, useState, useContext, useRef } from 'react';
import { useLocation } from 'react-router';
import { Spin } from 'antd';
import { LeftArrowIcon } from '@assets/icon';
import { Message } from '@shared/utils/message';
import { isJsonString, updateChatId } from '@shared/utils/common';
import ChatMessage from './components/chat-message';
import SendEditor from './components/send-editor/send-editor.jsx';
import CheckGroup from './components/check-group';
import Inspiration from './components/inspiration';
import { initChat } from './common/config';
import { AippContext } from '../aippIndex/context';
import {
  updateFlowInfo,
  stopInstance,
  getChatRecentLog,
} from '@shared/http/aipp';
import { sseChat, saveContent, getReportInstance } from '@shared/http/sse';
import {
  historyChatProcess,
  inspirationProcess,
  reportProcess,
  messageProcess,
  messageProcessNormal,
  beforeSend,
  deepClone,
  scrollBottom
} from './utils/chat-process';
import './styles/chat-preview.scss';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import {
  setAtChatId,
  setChatId,
  setChatList,
  setChatRunning,
  setFormReceived,
} from '@/store/chatStore/chatStore';
import { storage } from '@shared/storage';
import { EventSourceParserStream } from '@shared/event-source/stream';
import { isBusinessMagicCube } from '@shared/utils/common';
import { useTranslation } from "react-i18next";

const ChatPreview = (props) => {
  const { t } = useTranslation();
  const { previewBack, chatType } = props;
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatId = useAppSelector((state) => state.chatCommonStore.chatId);
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
  const [loading, setLoading] = useState(false);
  const [groupType, setGroupType] = useState('share');
  const [showCheck, setShowCheck] = useState(false);
  const location = useLocation();
  let editorRef = React.createRef();
  let runningInstanceId = useRef('');
  let currentInfo = useRef();
  let feedRef = useRef();
  let testRef = useRef(false);
  let reportInstance = useRef('');
  let reportIContext = useRef(null);
  const listRef = useRef([]);
  const detailPage = location.pathname.indexOf('app-detail') !== -1;
  const chatStatus = ['ARCHIVED', 'ERROR'];
  const messageType = ['MSG', 'ERROR'];

  useEffect(() => {
    currentInfo.current = appInfo;
    return () => {
      closeConnected();
      dispatch(setChatList([]));
    };
  }, []);
  useEffect(() => {
    testRef.current = formReceived;
  }, [formReceived]);

  // 切换App时，chatId为应用上次会话id
  useEffect(() => {
    if (!appId) {
      return;
    }
    // 如果不是小魔方
    if (!isBusinessMagicCube(appId)) {
      dispatch(setChatId(storage.getChatId(appId)));
    } else {
      const dimensionId = storage.get('dimension')?.id;
      dispatch(setChatId(storage.getDimensionChatId(dimensionId)));
    }
  }, [appId, dimension]);

  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem) {
    let { prompt, promptArr } = inspirationProcess(tenantId, data, prompItem, appInfo);
    editorRef.current.setFilterHtml(prompt, promptArr);
  }
  // 获取历史会话
  async function initChatHistory() {
    listRef.current = [];
    if (!chatId) return;
    setLoading(true);
    try {
      const res = await getChatRecentLog(tenantId, chatId, appId);
      if (res.data && res.data.length) {
        let chatArr = historyChatProcess(res);
        listRef.current = deepClone(chatArr);
        await dispatch(setChatList(chatArr));
        feedRef.current.initFeedbackStatus('all');
        scrollToBottom();
      }
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    if (!currentInfo.current || currentInfo.current.id !== appInfo.id) {
      dispatch(setChatRunning(false));
      dispatch(setChatList([]));
      appInfo.name && !appInfo.notShowHistory && initChatHistory();
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
    if (atAppInfo) {
      reciveInitObj.appName = atAppInfo.name;
      reciveInitObj.appIcon = atAppInfo.attributes?.icon;
      reciveInitObj.isAt = true;
    }
    let arr = [...listRef.current, reciveInitObj];
    listRef.current = deepClone(arr);
    dispatch(setChatList(deepClone(arr)));
    dispatch(setChatRunning(true));
    scrollToBottom();
    if (showElsa) {
      let params = appInfo.flowGraph;
      window.agent
        .validate()
        .then(async () => {
          const res = await updateFlowInfo(tenantId, appId, params);
          if (res.code !== 0) {
            onStop('更新grpha数据失败');
          } else {
            chatMissionStart(value, type);
          }
        })
        .catch((err) => {
          Message({ type: 'warning', content: '请输入必填项' });
          onStop('对话失败');
        });
    } else {
      chatMissionStart(value, type);
    }
  };
  // 启动任务
  const chatMissionStart = async (value, type) => {
    let chatParams = {
      'app_id': appId,
      'question': type ? '请解析以下文件' : value,
      'context': {
        'use_memory': useMemory,
        dimension: dimension.value,
      }
    };
    if (chatId) {
      chatParams['chat_id'] = chatId;
    }
    if (atAppId) {
      chatParams.context.at_chat_id = atChatId;
      chatParams.context.at_app_id = atAppInfo.id;
    }
    if (type) {
      chatParams.context['$[FileDescription]$'] = value;
    }
    queryInstance(chatParams);
  };
  // 开始对话
  const queryInstance = async (params, type = undefined, instanceId = '') => {
    runningInstanceId.current = null;
    let response;
    if (type === 'clar') {
      response = await saveContent(tenantId, instanceId, params);
    } else {
      response = await sseChat(tenantId, params, chatType);
    }
    if (response.status !== 200) {
      onStop('启动会话失败');
      return;
    };
    chatStreaming(response);
  }
  // sse流式输出
  const chatStreaming = async (response) => {
    const reader = response?.body?.pipeThrough(new TextDecoderStream())
      .pipeThrough(new EventSourceParserStream()).getReader();
    while (true) {
      const sseResData = await reader?.read();
      const { done, value } = sseResData;
      if (!done) {
        try {
          let msgStr = value.data;
          const receiveData = JSON.parse(msgStr);
          if (receiveData.code) {
            closeConnected();
            onStop(val.msg || '会话失败');
            break;
          } else {
            sseReceiveProcess(receiveData);
          }
        } catch (e) {
          console.info(e);
        }
      } else {
        break;
      };
    };
  }
  // sse接收消息回调
  const sseReceiveProcess = (messageData) => {
    try {
      // 初始化设置instanceId
      if (messageData.status === 'READY') {
        runningInstanceId.current = messageData.instance_id;
        return;
      }
      // 用户自勾选
      if (messageData.memory === 'UserSelect') {
        selfSelect(messageData.instanceId, messageData.initContext);
        return;
      }
      // 智能表单
      if (messageData.formAppearance?.length) {
        let obj = messageProcess(runningInstanceId.current, messageData, atAppInfo);
        chatForm(obj);
        saveLocalChatId(messageData);
        return;
      }
      // 普通日志
      messageData.answer?.forEach((log) => {
        if (log.type === 'FORM') {
          let obj = messageProcess(runningInstanceId.current, log.content, atAppInfo);
          chatForm(obj);
          saveLocalChatId(messageData);
        }
        if (messageType.includes(log.type)) {
          let { msg, recieveChatItem } = messageProcessNormal(log, atAppInfo);
          if (log.msgId !== null) {
            chatSplicing(log, msg, recieveChatItem, messageData.status);
          } else {
            chatStrInit(msg, recieveChatItem, messageData.status);
          }
        }
      });
      if (chatStatus.includes(messageData.status)) {
        saveLocalChatId(messageData);
        dispatch(setChatRunning(false));
      }
    } catch (err) {
      onStop(t('dataParseError'));
      dispatch(setChatRunning(false));
    }
  };
  // sse回调保存chatId
  const saveLocalChatId = (data) => {
    let { chat_id, at_chat_id } = data;
    if (chat_id) {
      updateChatId(chat_id, appId, dimension);
      dispatch(setChatId(chat_id));
    }
    if (at_chat_id) {
      dispatch(setAtChatId(at_chat_id));
    }
  };
  // 聊天表单
  const chatForm = (chatObj) => {
    const idx = listRef.current.length - 1;
    listRef.current.splice(idx, 1, chatObj);
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
  };
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
        memories: JSON.stringify(memoriesList),
      },
    };
    try {
      const startes = await getReportInstance(tenantId, reportInstance.current, params);
      if (startes.status !== 200) {
        onStop('对话失败');
        return;
      };
      listRef.current[listRef.current.length - 1].loading = true;
      dispatch(setChatList(deepClone(listRef.current)));
      chatStreaming(startes);
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
    initObj.finished = status === 'ARCHIVED';
    idx = listRef.current.length - 1;
    if (testRef.current) {
      initObj.messageType = 'form';
      listRef.current.push(initObj);
      dispatch(setFormReceived(false));
    } else {
      listRef.current.splice(idx, 1, initObj);
    }
    dispatch(setChatList(deepClone(listRef.current)));
  }
  // 流式输出拼接
  function chatSplicing(log, msg, initObj, status) {
    let msgId = log.msgId;
    let currentChatItem = listRef.current.filter((item) => item.logId === msgId)[0];
    if (currentChatItem) {
      let index = listRef.current.findIndex((item) => item.logId === log.msgId);
      let item = listRef.current[index];
      let str = '';
      let { content } = currentChatItem;
      str = content + msg;
      item.content = str;
      if (status === 'ARCHIVED') {
        item.finished = true;
      }
      dispatch(setChatList(deepClone(listRef.current)));
    } else {
      chatStrInit(msg, initObj, status);
    }
  }
  // 显示问答组
  function setEditorShow(val, type = 'share') {
    !val && setCheckedList([]);
    setShowCheck(val);
    val && setGroupType(type);
  }
  // 终止对话成功回调
  function onStop(str) {
    let item = listRef.current[listRef.current.length - 1];
    item.content = str;
    item.recieveType = undefined;
    item.loading = false;
    item.messageType = 'form';
    dispatch(setChatList(deepClone(listRef.current)));
    dispatch(setChatRunning(false));
  }
  // 终止进行中的对话
  async function chatRunningStop(params) {
    let str = params.content ? params.content : '已终止对话';
    if (!runningInstanceId.current) return;
    const res = await stopInstance(tenantId, runningInstanceId.current, { content: str });
    if (res.code === 0) {
      onStop(res.data || str);
      Message({ type: 'success', content: '已终止对话' });
      closeConnected();
      return res.code;
    } else {
      Message({ type: 'error', content: '终止对话失败' });
    }
  }
  // 澄清表单重新对话
  function questionClarConfirm(params, instanceId) {
    queryInstance(params, 'clar', instanceId);
  }
  // 溯源表单重新对话
  function conditionConfirm(response) {
    const reciveInitObj = deepClone(initChat);
    let arr = [...listRef.current, reciveInitObj];
    listRef.current = arr;
    dispatch(setChatList(deepClone(arr)));
    dispatch(setChatRunning(true));
    scrollToBottom();
    chatStreaming(response);
  }
  function scrollToBottom() {
    setTimeout(() => {
      scrollBottom();
    }, 50);
  }
  // 关闭链接
  const closeConnected = () => {
    dispatch(setChatRunning(false));
  }
  return (
    <div
      className={`
        chat-preview 
        ${showElsa ? 'chat-preview-elsa' : ''} 
        ${detailPage ? 'chat-preview-inner' : ''} 
        ${(showElsa && inspirationOpen) ? 'chat-preview-mr' : ''}`}
    >
      <Spin spinning={loading}>
        <span className='icon-back' onClick={previewBack}>
          {showElsa && <LeftArrowIcon />}
        </span>
        <div className={`chat-inner ${!detailPage ? 'chat-page-inner' : ''}`}>
          <div className={`chat-inner-left ${inspirationOpen ? 'chat-left-close' : 'no-border'}`}>
            <ChatMessage
              feedRef={feedRef}
              chatRunningStop={chatRunningStop}
              setCheckedList={setCheckedList}
              setEditorShow={setEditorShow}
              conditionConfirm={conditionConfirm}
              chatStreaming={chatStreaming}
              questionClarConfirm={questionClarConfirm}
              showCheck={showCheck} />
            {showCheck ?
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
                chatType={chatType}
                setEditorShow={setEditorShow}
                inspirationOpen={inspirationOpen}
              />
            }
          </div>
          <div className={`chat-inner-right ${inspirationOpen ? 'chat-right-close' : ''}`}>
            <Inspiration inspirationClick={onSend} setEditorSelect={setEditorSelect} />
          </div>
        </div>
      </Spin>
    </div>
  );
};

export default ChatPreview;
