import React, {
  useEffect,
  useState,
  useRef,
  useContext,
  useImperativeHandle,
} from "react";
import { AudioIcon, SendIcon, DeleteContentIcon } from '@/assets/icon';
import $ from "jquery";
import { Message } from "@shared/utils/message";
import { AippContext } from "../../../aippIndex/context";
import { httpUrlMap } from "@shared/http/httpConfig";
import { messagePaste } from './utils';
import HistoryChat from "../history-chat";
import HistoryChatDrawer from "../history-chat";
import Recommends from './components/recommends';
import EditorBtnHome from './components/editor-btn-home';
import "@shared/utils/rendos";
import "../../styles/send-editor.scss";

const SendEditor = (props) => {
  const {
    onSend,
    onStop,
    onClear,
    filterRef
  } = props;
  const [ selectItem, setSelectItem ] = useState({});
  const [ selectDom, setSelectDom ] = useState();
  const [ showSelect, setShowSelect ] = useState(false);
  const [ showClear, setShowClear ] = useState(false);
  const [ openHistory, setOpenHistory ] = useState(false);
  const [ positionConfig, setPositionConfig ] = useState({});
  const { chatRunning }  = useContext(AippContext);
  const { WS_AUDIO_URL } = httpUrlMap[process.env.NODE_ENV];
  const editorRef = useRef(null);
  const recording = useRef(false);
  // 编辑器change事件
  function messageChange() {
    setShowClear(() => {
      return editorRef.current.innerText.trim().length > 0
    })
  }
  // 清除内容
  function clearContent() {
    editorRef.current.innerText = "";
    setShowClear(false);
  }
  // 快捷发送
  function messageKeyDown(e) {
    if (e.ctrlKey && e.keyCode === 13) {
      e.preventDefault();
      document.execCommand("insertLineBreak");
    } else if (e.keyCode === 13) {
      e.preventDefault();
      sendMessage();
    }
  }
  // 发送消息
  function sendMessage() {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    let chatContent = document.getElementById("ctrl-promet").innerText;
    onSend(chatContent);
    editorRef.current.innerText = "";
    setShowClear(false);
  }
  // 设置灵感大全下拉
  function setFilterHtml(prompt, promptMap) {
    const editorDom = document.getElementById("ctrl-promet");
    editorDom.innerHTML = prompt;
    bindEvents(promptMap);
  }
  // 绑定下拉事件
  function bindEvents(promptMap) {
    $('body').on('click', '.chat-focus', ($event) => {
      let filterType = $($event.target).attr('data-type');
      let selectItem =  promptMap.filter(item => item.var === filterType)[0];
      setPositionConfig($event.target.getBoundingClientRect());
      setSelectItem(selectItem);
      setSelectDom($event.target);
      setShowSelect(true);
    });
    $("body").on("click", (event) => {
      let clickTarget = $(event.target);
      let chatPopup = $('.chat-focus');
      if (
        !clickTarget.closest(chatPopup).length
      ) {
        setShowSelect(false);
      }
    });
  }
  // 文件自动发送
  function fileSend(fileResult, fileType) {
    onSend(fileResult, fileType);
  }
  useImperativeHandle(filterRef, () => {
    return {
      setFilterHtml: setFilterHtml,
    };
  });
  // 语音实时转文字
  let recorderHome = null;
  let intervalData = null;
  const onRecord = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    if (!recording.current) {
      window.HZRecorder.get((rec) => {
        recorderHome = rec;
        recorderHome.start();
      });
      recording.current = true;
      let conn = new WebSocket(WS_AUDIO_URL);
      conn.onopen = (evt) => {
        if (conn.readyState === 1) {
          intervalData = setInterval(() => {
            let res = recorderHome.getBlob();
            conn.send(res);
          }, 1000);
        }
      };
      conn.onmessage = (evt) => {
        if (evt.data.trim().length) {
          const editorDom = document.getElementById("ctrl-promet");
          editorDom.innerHTML = evt.data.trim();
        }
      };
      conn.onerror = (err) => {
        Message({ type: "error", content: "语音转文字失败" });
      };
      conn.onclose = (err) => {
        recorderHome.stop();
        recording.current = false;
        clearInterval(intervalData);
      };
    }
  }
  return <>{(
    <div className='send-editor-container'>
      <Recommends onSend={onSend}/>
      <div className='editor-inner'>
        <EditorBtnHome 
          setOpenHistory={setOpenHistory}
          clear={onClear}
          fileCallBack={fileSend}
        />
        { chatRunning && 
          <div className="editor-stop" onClick={onStop}>
            <img src="/src/assets/images/ai/stop.png" alt="" />
            <span>停止响应</span>
          </div>
        }
        <div className='editor-input' id="drop">
          <div
            className="chat-promet-editor"
            id="ctrl-promet"
            ref={ editorRef }
            contentEditable={ true }
            onInput={messageChange}
            onKeyDown={messageKeyDown}
            onPaste={messagePaste}
          />
          <div className='send-icon' onClick={ sendMessage }>
            <SendIcon />
          </div>
          <div className='audio-icon' onClick={onRecord}><AudioIcon /></div>
          { showClear && <div className='send-icon clear-icon' onClick={clearContent}><DeleteContentIcon /></div> }
        </div>
      </div>
      <div className='chat-tips'>
          - 所有内容均由人工智能大模型生成，存储产品内容准确性参照存储产品文档 -
      </div>
     { showSelect &&  (
      <EditorSelect
        chatSelectDom={selectDom}
        chatSelectItem={selectItem}
        positionConfig={positionConfig}
        clearMove={() => setShowSelect(false)} />
     )}
    </div>
  )}</>
};

export default SendEditor;
