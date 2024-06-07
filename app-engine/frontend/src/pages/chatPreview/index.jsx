import React, { useEffect, useState, useContext, useRef } from "react";
import { useLocation } from "react-router";
import { Spin } from "antd";
import { LeftArrowIcon } from "@assets/icon";
import { Message } from "@shared/utils/message";
import { isJsonString } from "@shared/utils/common";
import ChatMessage from "./components/chat-message";
import SendEditor from "./components/send-editor/send-editor.jsx";
import CheckGroup from "./components/check-group.jsx";
import Inspiration from "./components/inspiration.jsx";
import { initChat} from "./common/config";
import { AippContext } from "../aippIndex/context";
import {
  aippDebug,
  updateFlowInfo,
  getRecentInstances,
  stopInstance,
  getReportInstance
} from '@shared/http/aipp';
import { 
  historyChatProcess, 
  inspirationProcess,
  reportProcess,
  messageProcess,
  messageProcessNormal,
  beforeSend } from './utils/chat-process';
import "./styles/chat-preview.scss";
import { creatChat, tenantId, updateChat } from "../../shared/http/chat.js";

const ChatPreview = (props) => {
  const { previewBack } = props;
  const { chatRunning, aippInfo, appId, tenantId,chatList, 
    setChatList,chatId,setChatId,showElsa,chatType,
    inspirationOpen,setInspirationOpen,setChatRunning} = useContext(AippContext);
  const [checkedList, setCheckedList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [groupType, setGroupType] = useState("share");
  const [showCheck, setShowCheck] = useState(false);
  const location = useLocation();
  let editorRef = React.createRef();
  let runningInstanceId = useRef("");
  let runningVersion = useRef("");
  let runningAppid = useRef("");
  let childInstanceStop = useRef(false);
  let wsCurrent = useRef(null);
  let reportInstance = useRef('');
  let reportIContext = useRef(null);
  const listRef = useRef(null);
  const chatPage =  location.pathname.indexOf('chat') !== -1;

  useEffect(() => {
    !chatType && setInspirationOpen(true);
  }, []);

  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem) {
    let { prompt, promptArr } = inspirationProcess(tenantId, data, prompItem);
    editorRef.current.setFilterHtml(prompt, promptArr);
  }
  // 获取历史会话
  async function initChatHistory() {
    setChatList(() => {
      listRef.current = [];
      return [];
    });
    setLoading(true);
    try {
      const res = await getRecentInstances(tenantId, appId, 'preview');
      if (res.data && res.data.length) {
        let chatArr = historyChatProcess(res);
        setChatList(() => {
          listRef.current = [...chatArr];
          return [...chatArr];
        });
      }
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    // 清空聊天记录
    setChatRunning(false);
    setChatId(null);
    setChatList([]);
    // 初始化聊天记录，目前所有chat聊天记录均未调用initChatHistory()
    (aippInfo.name && !aippInfo.notShowHistory) && initChatHistory();
  }, [aippInfo]);
  
  // 发送消息
  const onSend = (value, type = undefined) => {
    const sentItem = beforeSend(chatRunning, value, type);
    if (sentItem) {
      setChatList(() => {
        let arr = [...chatList, sentItem];
        listRef.current = arr;
        return arr;
      });
      sendMessageRequest(value, type);
    }
  };
  // 发送消息
  const sendMessageRequest = async (value, type) => {
    const reciveInitObj = JSON.parse(JSON.stringify(initChat));
    reciveInitObj.type = "recieve";
    reciveInitObj.loading = true;
    setChatList(() => {
      let arr = [...listRef.current, reciveInitObj];
      listRef.current = arr;
      return arr;
    });
    setChatRunning(true);
    if (showElsa) {
      let params = aippInfo.flowGraph;
      window.agent
        .validate()
        .then(async () => {
          const res = await updateFlowInfo(tenantId, appId, params);
          if (res.code !== 0) {
            onStop("更新grpha数据失败");
          } else {
            getAippAndVersion(value, type);
          }
        })
        .catch((err) => {
          Message({ type: "warning", content: "请输入必填项" });
          onStop("对话失败");
        });
    } else {
      getAippAndVersion(value, type);
    }
  };
  // 获取aipp_id和version
  async function getAippAndVersion(value, type) {
    try {
      const debugRes = await aippDebug(tenantId, appId, aippInfo);
      if (debugRes.code === 0) {
        chatMissionStart(debugRes.data, value, type);
      } else {
        onStop(debugRes.msg || "获取aippId失败");
      }
    } catch {
      onStop("获取aippId失败");
    }
  }
  // 启动任务
  const chatMissionStart = async (res, value, type) => {
    let { aipp_id, version } = res;
    let params = type?{ initContext: { "$[FileDescription]$": value } }:{ initContext: { Question: value } };
    try {
      const requestBody={
        app_id:aipp_id,
        app_version:version,
        init_context:params,
      }
      let res;
      if(chatId){
        res= await updateChat(tenantId, chatId, requestBody);
      } else {
        res= await creatChat(tenantId, requestBody);
        setChatId(res?.data?.chat_id);
      }
      childInstanceStop.current = false;
      const instanceId = res?.data?.current_instance_id;
      if (instanceId) {
        childInstanceStop.current = false;
        queryInstance(aipp_id, version, instanceId);
      } else {
        onStop("对话失败");
      }
    } catch {
      onStop("对话失败");
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
      setChatRunning(false);
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
          let obj = messageProcess(aipp_id, instanceId, version, messageData);
          chatForm(obj);
          return;
        }
        // 普通日志
        messageData.aippInstanceLogs?.forEach(log => {
          if (log.logData && log.logData.length) {
            let { msg, recieveChatItem } = messageProcessNormal(log, instanceId);
            if (log.msgId !== null) {
              chatSplicing(log, msg, recieveChatItem, messageData.status);
            } else {
              chatStrInit(msg, recieveChatItem, messageData.status);
            }
          }
        })
        if (['ARCHIVED'].includes(messageData.status)) {
          setChatRunning(false);
        }
        if (['ERROR'].includes(messageData.status)) {
          setChatRunning(false);
        }
      } catch (err){
        onStop('数据解析异常');
        setChatRunning(false);
      }
    }
  }
  // 聊天表单
  const chatForm = (chatObj) => {
    const idx = listRef.current.length - 1;
    listRef.current.splice(idx, 1, chatObj);
    setChatList(() => {
      let arr = [...listRef.current];
      listRef.current = arr;
      return arr;
    });
    setChatRunning(false);
  }
  // 用户自勾选
  function selfSelect(instanceId, initContext) {
    reportInstance.current = instanceId;
    reportIContext.current = initContext;
    setChatList(() => {
      listRef.current.forEach(item => item.checked = false);
      let arr = [...listRef.current];
      listRef.current = arr;
      return arr;
    });
    onStop("请勾选对话");
    setCheckedList([]);
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
        setChatList(() => {
          let arr = [...listRef.current];
          listRef.current = arr;
          return arr;
        });
        queryInstance(runningAppid.current, runningVersion.current, instanceId);
      } else {
        onStop("启动任务失败");
      }
    } catch {
      onStop("启动任务失败");
    } finally {
      setEditorShow(false);
    }
  }
  // 流式输出
  function chatStrInit(msg, initObj, status) {
    if (isJsonString(msg)) {
      let msgObj = JSON.parse(msg);
      if (msgObj.chartData && msgObj.chartType) {
        initObj.chartConfig = msgObj;
      }
    }
    initObj.loading = false;
    initObj.finished = (status === 'ARCHIVED');
    const idx = listRef.current.length - 1;
    listRef.current.splice(idx, 1, initObj);
    setChatList(() => {
      let arr = [...listRef.current];
      listRef.current = arr;
      return arr;
    });
  }
  // 流式输出拼接
  function chatSplicing(log, msg, initObj, status) {
    let currentChatItem = listRef.current.filter(
      (item) => item.logId === log.msgId
    )[0];
    if (currentChatItem) {
      let index = listRef.current.findIndex((item) => item.logId === log.msgId);
      let str = "";
      let { content } = currentChatItem;
      str = content + msg;
      listRef.current[index].content = str;
      listRef.current[index].finished = (status === 'ARCHIVED');
      setChatList(() => {
        let arr = [...listRef.current];
        listRef.current = arr;
        return arr;
      });
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
  function onStop(content) {
    setChatList(() => {
      let item = listRef.current[listRef.current.length - 1];
      item.content = content;
      item.loading = false;
      return listRef.current;
    });
    setChatRunning(false);
  }
  // 终止进行中的对话
  async function chatRunningStop() {
    const res = await stopInstance(tenantId, runningInstanceId.current);
    if (res.code === 0) {
      onStop("已终止对话");
      wsCurrent.current?.close();
      wsCurrent.current = null;
      Message({ type: "success", content: "已终止对话" });
    } else {
      Message({ type: "error", content: "终止对话失败" });
    }
  }

  return (
    <div className={`
        chat-preview 
        ${ showElsa ? 'chat-preview-elsa chat-preview-shadow': ''} 
        ${ !chatPage ? 'chat-preview-inner' : '' } 
        ${(showElsa && inspirationOpen) ? 'chat-preview-mr' : ''}`}
    >
      <Spin spinning={loading}>
        <span className="icon-back" onClick={previewBack}>
          { showElsa && <LeftArrowIcon /> }
        </span>
        <div className={ `chat-inner ${ chatPage ? 'chat-page-inner' : ''}`}>
          <div className={ `chat-inner-left ${ inspirationOpen ? 'chat-left-close' : 'no-border'}` }>
            <ChatMessage
              setCheckedList={setCheckedList}
              setEditorShow={setEditorShow}
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
