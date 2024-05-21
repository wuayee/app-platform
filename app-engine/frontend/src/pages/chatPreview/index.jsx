import React, { useEffect, useState, useContext, useRef } from "react";
import { useLocation } from "react-router";
import { Spin } from "antd";
import { LeftArrowIcon } from "@assets/icon";
import { Message } from "../../shared/utils/message";
import ChatMessage from "./components/chat-message.jsx";
import SendEditor from "./components/send-editor.jsx";
import CheckGroup from "./components/check-group.jsx";
import Inspiration from "./components/inspiration.jsx";
import {
  initChat,
  chatMock,
  chatMock3,
  codeMock,
  formMock,
} from "./common/config";
import { AippContext } from "../aippIndex/context";
import {
  aippDebug,
  aippStart,
  reGetInstance,
  updateFlowInfo,
  getRecentInstances,
  clearInstance,
  stopInstance,
  queryInspirationSelect,
} from "../../shared/http/aipp";
import { httpUrlMap } from "../../shared/http/httpConfig";
import left from "../../assets/images/left.png";
import "./styles/chat-preview.scss";

const { WS_URL } = httpUrlMap[process.env.NODE_ENV];
const ChatPreview = (props) => {
  const { chatStatusChange, chatType, previewBack } = props;
  const { showElsa, chatRunning, prompValue, aippInfo, appId, tenantId } =
    useContext(AippContext);
  const [chatList, setChatList] = useState([]);
  const [checkedList, setCheckedList] = useState([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [groupType, setGroupType] = useState("share");
  const [sessionName, setSessionName] = useState(["default"]);
  const [showCheck, setShowCheck] = useState(false);
  const [requestLoading, setRequestLoading] = useState(false);
  const location = useLocation();
  const chatInitObj = JSON.parse(JSON.stringify(initChat));
  let editorRef = React.createRef();
  let timerRef = useRef(null);
  let regex = /{{(.*?)}}/g;
  let runningInstanceId = useRef("");
  let runningVersion = useRef("");
  let runningAppid = useRef("");
  let childInstanceIdArr = useRef([]);
  let childBackInstanceIdArr = useRef([]);
  let childInstanceStop = useRef(false);
  let isChatRunning = useRef(false);

  // 灵感大全点击
  useEffect(() => {
    if (prompValue.name && prompValue.auto) {
      onSend(prompValue.prompt);
      return;
    }
    let result = [];
    let match;
    while ((match = regex.exec(prompValue.prompt))) {
      result.push(match[1]);
    }
    if (result.length) {
      setEditorSelect(result, prompValue);
    } else {
      const editorDom = document.getElementById("ctrl-promet");
      editorDom.innerHTML = prompValue.prompt || "";
    }
  }, [prompValue.key]);
  useEffect(() => {
    !chatType && setOpen(true);
  }, []);
  useEffect(() => {
    (aippInfo.name && !aippInfo.notShowHistory) && initChatHistory();
  }, [aippInfo])
  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem) {
    let { prompt, promptVarData } = prompItem;
    let promptArr = [];
    data.forEach(async (item) => {
      let replaceStr = `{{${item}}}`;
      let selectItem = promptVarData.filter((sItem) => sItem.var === item)[0];
      let options = [];
      let selectStr = "";
      if (selectItem.sourceType === "fitable") {
        let params = { appId, appType: "PREVIEW" };
        const res = await queryInspirationSelect(
          tenantId,
          "GetQAFromLog",
          params
        );
        if (res.code === 0) {
          options = res.data || [];
        }
        selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${
          selectItem.var || ""
        }</div>`;
      } else {
        options = selectItem ? selectItem.sourceInfo.split(";") : [];
        options = options.filter((item) => item.length > 0);
        selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${
          options[0] || ""
        }</div>`;
      }
      selectItem.options = options;
      promptArr.push(selectItem);
      prompt = prompt.replaceAll(replaceStr, selectStr);
    });
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
      let type =
        location.pathname.indexOf("chat") === -1 ? "preview" : "normal";
      const res = await getRecentInstances(tenantId, appId, type);
      if (res.data && res.data.length) {
        let chatArr = [];
        res.data.forEach((item) => {
          let questionObj = { type: "send", sendType: "text" };
          let { msg } = JSON.parse(item.question.logData);
          questionObj.logId = item.question.logId;
          questionObj.content = msg;
          chatArr.push(questionObj);
          if (item.instanceLogBodies.length) {
            item.instanceLogBodies.forEach((aItem) => {
              const regex = /```markdown(.*?)```/g;
              const replacedArr = aItem.logData.match(regex);
              let markdowned = aItem.logData.indexOf("```");
              if (replacedArr && replacedArr.length) {
                replacedArr.forEach((item) => {
                  let str = item.substring(11, item.length - 3);
                  aItem.logData = aItem.logData.replace(item, str);
                });
              }
              let { msg } = JSON.parse(aItem.logData);
              let answerObj = {
                content: msg,
                loading: false,
                openLoading: false,
                logId: aItem.logId,
                markdownSyntax: markdowned !== -1,
                type: "recieve",
              };
              if (isJsonString(msg)) {
                let msgObj = JSON.parse(msg);
                if (msgObj.chartData && msgObj.chartType) {
                  answerObj.chartConfig = msgObj;
                }
              }
              chatArr.push(answerObj);
            });
          } else {
            let answerObj = { type: "recieve", content: "获取回答失败" };
            chatArr.push(answerObj);
          }
        });
        setChatList(() => {
          listRef.current = [...chatArr];
          return [...chatArr];
        });
      }
    } finally {
      setLoading(false);
    }
  }
  // 发送消息
  const onSend = (value, type = undefined) => {
    if (!type && !value.trim().length) {
      return;
    }
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    chatInitObj.type = "send";
    if (type) {
      value.file_name = decodeURI(value.file_name);
      chatInitObj.sendType = type;
      chatInitObj.content = JSON.stringify(value);
    } else {
      chatInitObj.sendType = "text";
      chatInitObj.content = value;
    }
    setChatList(() => {
      let arr = [...chatList, chatInitObj];
      listRef.current = arr;
      return arr;
    });
    sendMessageRequest(value, type);
  };
  // 发送消息
  const listRef = useRef(null);
  const sendMessageRequest = async (value, type) => {
    const reciveInitObj = JSON.parse(JSON.stringify(initChat));
    reciveInitObj.type = "recieve";
    reciveInitObj.loading = true;
    reciveInitObj.content = '回答生成中';
    isChatRunning.current = false;
    setChatList(() => {
      let arr = [...listRef.current, reciveInitObj];
      listRef.current = arr;
      return arr;
    });
    chatStatusChange(true);
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
        onStop("对话失败");
      }
    } catch {
      onStop("对话失败");
    }
  }
  // 启动任务
  const chatMissionStart = async (res, value, type) => {
    let { aipp_id, version } = res;
    let params = {};
    if (type) {
      params = { initContext: { "$[FileDescription]": value } };
    } else {
      params = { initContext: { Question: value } };
    }
    try {
      const startes = await aippStart(tenantId, aipp_id, version, params);
      if (startes.code === 0 && startes.data) {
        isChatRunning.current = true;
        childInstanceStop.current = false;
        let instanceId = startes.data;
        queryInstance(aipp_id, version, instanceId);
      } else {
        onStop("对话失败");
      }
    } catch {
      onStop("对话失败");
    }
  };
  // 开始对话(循环主流程)
  const queryInstance = (aipp_id, version, instanceId) => {
    runningInstanceId.current = instanceId;
    runningVersion.current = version;
    runningAppid.current = aipp_id;
    const ws = new WebSocket(`${WS_URL}?aippId=${aipp_id}?version=${version}`);
    ws.onerror = () => {
      onStop('对话失败');
    }
    ws.onopen = () => {
      ws.send(JSON.stringify({'aippInstanceId': instanceId}));
    }
    ws.onmessage = ({ data }) => {
      let messageData = {};
      try {
        messageData = JSON.parse(data);
        const logDataList = messageData.aippInstanceLogs || [];
        logDataList.forEach(log => {
          if (log.logData && log.logData.length) {
            const regex = /```markdown(.*?)```/g;
            const replacedArr = log.logData.match(regex);
            let markdowned = log.logData.indexOf('```');
            if (replacedArr && replacedArr.length) {
              replacedArr.forEach(item => {
                let str = item.substring(11, item.length - 3);
                log.logData = log.logData.replace(item, str);
              });
            }
            let { msg } = JSON.parse(log.logData);
            let initObj = {
              content: msg,
              loading: false,
              openLoading: false,
              logId: log.msgId || -1,
              markdownSyntax: markdowned !== -1,
              type: 'recieve',
            }
            if (log.msgId !== null) {
              socketChat2(log, msg, initObj);
            } else {
              socketChat(msg, initObj);
            }
          }
        })
        if (['ERROR', 'ARCHIVED'].includes(messageData.status)) {
          ws.close();
        }
      } catch (err){
        onStop('数据解析异常');
        ws.close();
      }
    }
    ws.onclose = () => {
      chatStatusChange(false);
      isChatRunning.current = false;
    }
  }
  // 主流程轮训回调
  function callback(res, formData) {
    printLogs(res.data.instance_log);
    if (formData) {
      clearAgentEffects();
    } else if (res.data.status === "ERROR" || res.error || res.data.error) {
      clearAgentEffects();
    } else if (res.data.status === "ARCHIVED") {
      clearAgentEffects();
    }
  }
  // 流式输出
  function socketChat(msg, initObj) {
    if (isJsonString(msg)) {
      let msgObj = JSON.parse(msg);
      if (msgObj.chartData && msgObj.chartType) {
        initObj.chartConfig = msgObj;
      }
    }
    initObj.loading = false;
    const idx = listRef.current.length - 1;
    listRef.current.splice(idx, 1, initObj);
    setChatList(() => {
      let arr = [...listRef.current];
      listRef.current = arr;
      return arr;
    });
  }
  // 流式输出2
  function socketChat2(log, msg, initObj) {
    let currentChatItem = listRef.current.filter(
      (item) => item.logId === log.msgId
    )[0];
    if (currentChatItem) {
      let index = listRef.current.findIndex((item) => item.logId === log.msgId);
      let str = "";
      let { content } = currentChatItem;
      str = content + msg;
      listRef.current[index].content = str;
      setChatList(() => {
        let arr = [...listRef.current];
        listRef.current = arr;
        return arr;
      });
    } else {
      socketChat(msg, initObj);
    }
  }
  // 开启子流程
  const childTest = (aipp_id, version) => {
    let instanceId = childInstanceIdArr.current.at(-1);
    timerRef.current = setInterval(async () => {
      const res = await reGetInstance(tenantId, aipp_id, instanceId, "1.0.0");
      if (res.code !== 0) {
        onStop(res.msg || "子流程运行失败");
      }
      const formArgs = res.data.form_args;
      let hasChildInstanceId = childBackInstanceIdArr.current.filter(
        (item) => item === formArgs.childInstanceId
      );
      if (
        formArgs.childInstanceId &&
        formArgs.childInstanceId.length &&
        formArgs.childInstanceId !== "undefined" &&
        !hasChildInstanceId.length
      ) {
        clearInterval(timerRef.current);
        childInstanceIdArr.current.push(formArgs.childInstanceId);
        childBackInstanceIdArr.current.push(formArgs.childInstanceId);
        childTest(aipp_id, version);
      } else if (res.data.status === "ERROR" || res.error || res.data.error) {
        onStop("子流程运行失败");
      } else if (res.data.status === "ARCHIVED") {
        clearInterval(timerRef.current);
        childInstanceIdArr.current.pop();
        printLogs(res.data.instance_log);
        if (childInstanceIdArr.current.length) {
          childTest(aipp_id, version);
        } else {
          childInstanceStop.current = true;
          queryInstance(aipp_id, version, runningInstanceId.current);
        }
      }
    }, 3000);
  };
  // 开启新一轮对话
  function clearAgentEffects() {
    listRef.current.pop();
    setChatList(() => {
      let arr = [...listRef.current];
      listRef.current = arr;
      return arr;
    });
    clearInterval(timerRef.current);
    chatStatusChange(false);
  }
  // 对话日志显示
  let insLogIds = [];
  function printLogs(logList = []) {
    logList = logList.filter((item) => item.logType !== "QUESTION");
    logList.forEach((log) => {
      if (!insLogIds.includes(log.logId)) {
        insLogIds.push(log.logId);
        const regex = /```markdown(.*?)```/g;
        const replacedArr = log.logData.match(regex);
        let markdowned = log.logData.indexOf("```");
        if (replacedArr && replacedArr.length) {
          replacedArr.forEach((item) => {
            let str = item.substring(11, item.length - 3);
            log.logData = log.logData.replace(item, str);
          });
        }
        let { msg } = JSON.parse(log.logData);
        let initObj = {
          content: msg,
          loading: false,
          openLoading: false,
          logId: log.logId,
          markdownSyntax: markdowned !== -1,
          type: "recieve",
        };
        if (isJsonString(msg)) {
          let msgObj = JSON.parse(msg);
          if (msgObj.chartData && msgObj.chartType) {
            initObj.chartConfig = msgObj;
          }
        }
        const idx = listRef.current.length - 1;
        listRef.current.splice(idx, 1, initObj);
        setChatList(() => {
          let arr = [...listRef.current];
          listRef.current = arr;
          return arr;
        });
      }
    });
  }
  // 判断是否为json
  function isJsonString(str) {
    try {
      if (typeof JSON.parse(str) === "object") {
        return true;
      }
    } catch (e) {
      return false;
    }
    return false;
  }
  // 清除历史对话记录
  async function clearChat() {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    if (!chatList.length) {
      return;
    }
    let type = location.pathname.indexOf("chat") === -1 ? "preview" : "normal";
    try {
      setRequestLoading(true);
      const res = await clearInstance(tenantId, appId, type);
      if (res.code === 0) {
        setChatList([]);
        clearInterval(timerRef.current);
        insLogIds = [];
      }
    } finally {
      setRequestLoading(false);
    }
  }
  function openClick() {
    setSessionName("经营小魔方");
    setOpen(!open);
  }
  // 显示问答组
  function setEditorShow(val) {
    !val && setCheckedList([]);
    setShowCheck(val);
    selectAllClick(false);
    val && setGroupType("share");
  }
  // 设置全选取消全选
  function selectAllClick(val) {
    let arr = chatList.map((item) => {
      item.checked = val;
      return item;
    });
    let checkedList = arr.filter(
      (item) => item.checked && item.type === "send"
    );
    setCheckedList(checkedList);
    setChatList(arr);
  }
  // 终止对话成功回调
  function onStop(content) {
    setChatList(() => {
      let item = listRef.current[listRef.current.length - 1];
      item.content = content;
      item.loading = false;
      return listRef.current;
    });
    clearInterval(timerRef.current);
    chatStatusChange(false);
  }
  // 终止进行中的对话
  async function chatRunningStop() {
    setRequestLoading(true);
    try {
      clearInterval(timerRef.current);
      const res = await stopInstance(tenantId, runningInstanceId.current);
      if (res.code === 0) {
        onStop("已终止对话");
        Message({ type: "success", content: "已终止对话" });
      } else {
        queryInstance(
          runningAppid.current,
          runningVersion.current,
          runningInstanceId.current
        );
      }
    } finally {
      setRequestLoading(false);
    }
  }
  return <>{(
      <div className={[
        'chat-preview',
        showElsa ? 'chat-preview-elsa chat-preview-shadow' : null,
        location.pathname.indexOf('chat') === -1 ? 'chat-preview-inner' : null,
        (showElsa && open) ? 'chat-preview-mr' : null
        ].join(' ')}>
          <Spin spinning={loading}>
            { showElsa && (<span className="icon-back" onClick={previewBack}>
              <LeftArrowIcon />
            </span>) }
            <div className={['chat-inner', location.pathname.indexOf('chat') !== -1 ? 'chat-page-inner' : null].join(' ')}>
              <div className={['chat-inner-left', open ? 'chat-left-close' : 'no-border'].join(' ')}>
                <ChatMessage
                  chatList={chatList}
                  setEditorShow={setEditorShow}
                  setCheckedList={setCheckedList}
                  showCheck={showCheck}/>
                { showCheck ?
                  ( <CheckGroup
                      appId={appId}
                      tenantId={tenantId}
                      chatList={chatList}
                      setEditorShow={setEditorShow}
                      checkedList={checkedList}
                      totalNum={chatList.length}
                      selectAllClick={selectAllClick}
                      type={groupType}
                    />
                  ) : (
                    <SendEditor
                      filterRef={editorRef}
                      onSend={onSend}
                      onClear={clearChat}
                      openClick={openClick}
                      onStop={chatRunningStop}
                      chatType={chatType}
                      inspirationOpen={open}
                      requestLoading={requestLoading}
                      open={open}
                      openInspiration={openClick}
                    />
                  )
                }
              </div>
              <div className={['chat-inner-right', open ? 'chat-right-close' : null].join(' ')}>
                <Inspiration
                  open={open}
                  sessionName={sessionName}
                  chatType={chatType}>
                </Inspiration>
              </div>
            </div>
          </Spin>
      </div>
      )}
    </>
};

export default ChatPreview;
