
import React, { useEffect, useState, useContext, useRef } from 'react';
import { useLocation  } from 'react-router';
import { LeftArrowIcon } from '@assets/icon';
import { Message } from '../../shared/utils/message';
import ChatMessage from './components/chat-message.jsx';
import SendEditor from './components/send-editor.jsx';
import CheckGroup from './components/check-group.jsx';
import Inspiration from './components/inspiration.jsx';
import { initChat, chatMock, codeMock, formMock } from './common/config';
import { AippContext } from '../aippIndex/context';
import {
  aippDebug,
  aippStart,
  reGetInstance,
  updateFlowInfo,
  getRecentInstances,
  clearInstance,
  stopInstance,
  queryInspirationSelect } from '../../shared/http/aipp';
import left from '../../assets/images/left.png';
import './styles/chat-preview.scss';
import { ready } from 'jquery';

const ChatPreview = (props) => {
  const { chatStatusChange, chatType, previewBack } = props;
  const {
    showElsa, chatRunning,
    prompValue, aippInfo,
    appId, tenantId }  = useContext(AippContext);
  const [ chatList, setChatList ] = useState([]);
  const [ checkedList, setCheckedList ] = useState([]);
  const [ open, setOpen ] = useState(false);
  const [ groupType, setGroupType ] = useState('share')
  const [ sessionName, setSessionName ] = useState(['default']);
  const [ showCheck, setShowCheck] = useState(false);
  const location = useLocation();
  const chatInitObj = JSON.parse(JSON.stringify(initChat));
  let editorRef = React.createRef();
  let timerRef = useRef(null);
  let regex = /{{(.*?)}}/g;
  let runningInstanceId = '';
  let isChatRunning = false;
  let runningVersion = '';

  // 灵感大全点击
  useEffect(() => {
    if (prompValue.name && prompValue.auto) {
      onSend(prompValue.prompt);
      return
    }
    let result = [];
    let match;
    while (match = regex.exec(prompValue.prompt)) {
      result.push(match[1]);
    }
    if (result.length) {
      setEditorSelect(result, prompValue);
    } else {
      const editorDom = document.getElementById('ctrl-promet');
      editorDom.innerHTML = prompValue.prompt || '';
    }
  }, [prompValue.key]);
  useEffect(() => {
    !chatType && setOpen(true);
  }, []);
  useEffect(() => {
    aippInfo.name && initChatHistory();
  }, [aippInfo])
  // 灵感大全设置下拉列表
  function setEditorSelect(data, prompItem) {
    let { prompt, promptVarData } = prompItem;
    let promptArr = [];
    data.forEach(async (item) => {
      let replaceStr = `{{${item}}}`;
      let selectItem = promptVarData.filter(sItem => sItem.var === item)[0];
      let options = [];
      let selectStr = '';
      if (selectItem.sourceType === 'fitable') {
        let params = { appId, appType: 'PREVIEW' }
        const res = await queryInspirationSelect(tenantId, 'GetQAFromLog', params);
        options = res.data || [];
        selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${selectItem.var || ''}</div>`;
      } else {
        options = selectItem ? selectItem.sourceInfo.split(';') : [];
        selectStr = `<div class="chat-focus" contenteditable="false" data-type="${item}" style="min-width: 40px;">${options[0] || ''}</div>`;
      }
      selectItem.options = options;
      promptArr.push(selectItem);
      prompt = prompt.replaceAll(replaceStr, selectStr);
    })
    editorRef.current.setFilterHtml(prompt, promptArr);
  }
  // 获取历史会话
  async function initChatHistory() {
    const debugRes = await aippDebug(tenantId, appId, aippInfo);
    if (debugRes.code === 0) {
      let { aipp_id, version } = debugRes.data;
      const res = await getRecentInstances(tenantId, aipp_id, version);
      if (res.data && res.data.length) {
        let chatArr = [];
        res.data.forEach(item => {
          let questionObj = { type: 'send', sendType: 'text' };
          let { msg } = JSON.parse(item.question.logData);
          questionObj.logId = item.question.logId;
          questionObj.content = msg;
          chatArr.push(questionObj);
          if (item.instanceLogBodies.length) {
            item.instanceLogBodies.forEach(aItem => {
              let { msg } = JSON.parse(aItem.logData);
              let answerObj = { type: 'recieve' };
              answerObj.logId = aItem.logId;
              answerObj.content = msg;
              answerObj.loading = false;
              chatArr.push(answerObj);
            })
          } else {
            let answerObj = { type: 'recieve', content: '获取回答失败' };
            chatArr.push(answerObj);
          }
        })
        setChatList(() => {
          listRef.current = [ ...chatArr ];
          return [ ...chatArr ]
        });
      }
    }
  }
  // 发送消息
  const onSend = (value, type = undefined) => {
    if (!type && !value.trim().length) {
      return
    }
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    chatInitObj.type = 'send';
    if (type) {
      value.file_name = decodeURI(value.file_name);
      chatInitObj.sendType = type;
      chatInitObj.content = JSON.stringify(value);
    } else {
      chatInitObj.sendType = 'text';
      chatInitObj.content = value;
    }
    setChatList(() => {
      let arr = [ ...chatList, chatInitObj ];
      listRef.current = arr;
      return arr;
    });
    sendMessageRequest(value, type);
  }
  // 发送消息
  const listRef = useRef(null);
  const sendMessageRequest = async (value, type) => {
    const reciveInitObj = JSON.parse(JSON.stringify(initChat));
    reciveInitObj.type = 'recieve';
    reciveInitObj.loading = true;
    // reciveInitObj.loading = false;
    reciveInitObj.content = '回答生成中';
    // reciveInitObj.chartConfig = chatMock;
    // reciveInitObj.recieveType = 'form';
    // reciveInitObj.formConfig = formMock;
    isChatRunning = false;
    setChatList(() => {
      let arr = [ ...listRef.current, reciveInitObj ];
      listRef.current = arr;
      return arr
    });
    chatStatusChange(true);
    if (showElsa) {
      let params = aippInfo.flowGraph;
      try {
        await updateFlowInfo(tenantId, appId, params);
      } catch {
        onStop('更新grpha数据失败');
        return
      }
    }
    try {
      const debugRes = await aippDebug(tenantId, appId, aippInfo);
      if (debugRes.code === 0) {
        chatMissionStart(debugRes.data, value, type);
      } else {
        onStop('对话失败');
      }
    } catch {
      onStop('对话失败');
    }
  }
  // 启动任务
  const chatMissionStart = async (res, value, type) => {
    let { aipp_id, version } = res;
    let params = {}
    if (type) {
      params = { initContext: { '$[FileDescription]': value } }
    } else {
      params = { initContext: { 'Question': value } }
    }
    try {
      const startes = await aippStart(tenantId, aipp_id, version, params);
      if (startes.code === 0 && startes.data) {
        isChatRunning = true;
        let instanceId = startes.data;
        queryInstance(aipp_id, version, instanceId);
      } else {
        onStop('对话失败');
      }
    } catch {
      onStop('对话失败');
    }
  }
  // 开始对话
  const queryInstance = (aipp_id, version, instanceId) => {
    runningInstanceId = instanceId;
    runningVersion = version;
    timerRef.current = setInterval(async () => {
      const res = await reGetInstance(tenantId, aipp_id, instanceId, version);
      const formData = JSON.parse(res.data.form_metadata);
      const formArgs = res.data.form_args;
      printLogs(res.data.instance_log);
      if (formData) {
        clearAgentEffects();
      }
      if (res.data.status === 'ERROR' || res.error || res.data.error) {
        clearAgentEffects();
      }
      if (res.data.status === 'ARCHIVED') {
        clearAgentEffects();
      }
    }, 3000);
  }
  // 开启新一轮对话
  function clearAgentEffects() {
    listRef.current.pop();
    setChatList(() => {
      let arr = [ ...listRef.current ];
      listRef.current = arr;
      return arr
    });
    clearInterval(timerRef.current);
    chatStatusChange(false);
  }
  // 对话日志显示
  let insLogIds = [];
  function printLogs(logList = []) {
    logList = logList.filter(item => item.logType !== 'QUESTION');
    logList.forEach((log) => {
      if (!insLogIds.includes(log.logId)) {
        insLogIds.push(log.logId);
        let { msg } = JSON.parse(log.logData);
        const idx = listRef.current.length - 1;
        listRef.current.splice(idx, 0, {
          content: msg,
          loading: false,
          openLoading: false,
          logId: log.logId,
          type: 'recieve',
        });
        setChatList(() => {
          let arr = [ ...listRef.current ];
          listRef.current = arr;
          return arr
        });
      }
    });
  }
  // 清除历史对话记录
  async function clearChat() {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    };
    if (!chatList.length) {
      return
    }
    const debugRes = await aippDebug(tenantId, appId, aippInfo);
    if (debugRes.code === 0) {
      let { aipp_id, version } = debugRes.data;
      const res = await clearInstance(tenantId, aipp_id, version);
      if (res.code === 0) {
        setChatList([]);
        clearInterval(timerRef.current);
        insLogIds = [];
        Message({ type: 'success', content: '清除历史对话成功' })
      } else {
        Message({ type: 'error', content: res.msg || '清除历史对话失败' })
      }
    }
  }
  function openClick() {
    setSessionName('经营小魔方')
    setOpen(!open)
  }
  // 显示问答组
  function setEditorShow(val) {
    !val && setCheckedList([]);
    setShowCheck(val);
    selectAllClick(false);
    val && setGroupType('share');
  }
  // 设置全选取消全选
  function selectAllClick(val) {
    let arr = chatList.map((item) => {
      item.checked = val;
      return item;
    });
    let checkedList = arr.filter((item => item.checked && item.type === 'send'));
    setCheckedList(checkedList);
    setChatList(arr);
  }
  // 终止对话成功回调
  function onStop(content) {
    setChatList(() => {
      let item = listRef.current[listRef.current.length - 1];
      item.content =  content;
      item.loading = false;
      return listRef.current
    })
    clearInterval(timerRef.current);
    chatStatusChange(false);
  }
  // 终止进行中的对话
  async function chatRunningStop() {
    onStop('对话已终止');
    // const res = await stopInstance(tenantId, appId, runningInstanceId, runningVersion);
    // if (res.code === 0) {
    //   onStop('对话已终止');
    //   Message({ type: 'success', content: '对话已终止' });
    // } else {
    //   Message({ type: 'error', content: res.msg || '对话终止失败' })
    // }
  }
  return <>{(
      <div className={[
        'chat-preview',
        showElsa ? 'chat-preview-elsa chat-preview-shadow' : null,
        location.pathname.indexOf('chat') === -1 ? 'chat-preview-inner' : null,
        (showElsa && open) ? 'chat-preview-mr' : null
        ].join(' ')}>
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
                  setEditorShow={setEditorShow}
                  checkedList={checkedList}
                  totalNum={chatList.length}
                  selectAllClick={selectAllClick}
                  type={groupType}/> ) :
              (
                <SendEditor
                  filterRef={editorRef}
                  onSend={onSend}
                  onClear={clearChat}
                  onStop={chatRunningStop}
                  chatType={chatType}
                />
              )
            }
            <div className='chat-tips'> - 所有内容均由人工智能大模型生成，存储产品内容准确性参照存储产品文档 - </div>
          </div>
          <div className={['chat-inner-right', open ? 'chat-right-close' : null].join(' ')}>
            <div className='inspiratio-tag' onClick={openClick}>
              <img src={left} className={ !open ? 'img-trans' : null }  alt="" />
            </div>
            <Inspiration
              open={open}
              sessionName={sessionName}
              chatType={chatType}>
            </Inspiration>
          </div>
        </div>
        { location.pathname.indexOf('chat') === -1 && <div className="blue-div"></div> }
        { location.pathname.indexOf('chat') === -1 && <div className="pink-div"></div> }
      </div>
    )}
  </>
};

export default ChatPreview;
