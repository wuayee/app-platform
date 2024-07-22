import React, {
  useEffect,
  useState,
  useRef,
  useImperativeHandle,
  forwardRef,
} from 'react';
import { AudioIcon, AudioActiveIcon , SendIcon, DeleteContentIcon } from '@/assets/icon';
import $ from 'jquery';
import { Message } from '@shared/utils/message';
import { httpUrlMap } from '@shared/http/httpConfig';
import { messagePaste } from './utils';
import Recommends from './components/recommends';
import EditorBtnHome from './components/editor-btn-home';
import EditorSelect from './components/editor-selet';
import '@shared/utils/rendos';
import '../../styles/send-editor.scss';
import { useAppSelector, useAppDispatch } from '@/store/hook';
import { setUseMemory } from '@/store/common/common';
import {uploadChatFile,voiceToText} from '@shared/http/aipp'

const AudioBtn = forwardRef((props, ref) => {
  const [active, setActive] = useState(props.active || false);
  useImperativeHandle(ref, () => {
    return {
      active,
      setActive,
    }
  })

  return <>
    {active ? <AudioActiveIcon className='active-audio-btn' />: <AudioIcon />}
  </>
})

const SendEditor = (props) => {
  const {
    onSend,
    onStop,
    onClear,
    filterRef
  } = props;
  const dispatch = useAppDispatch();
  const [ selectItem, setSelectItem ] = useState({});
  const [ selectDom, setSelectDom ] = useState();
  const [ showSelect, setShowSelect ] = useState(false);
  const [ showClear, setShowClear ] = useState(false);
  const [ openHistory, setOpenHistory ] = useState(false);
  const [ positionConfig, setPositionConfig ] = useState({});
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const showMulti = useAppSelector((state) => state.commonStore.historySwitch);
  const editorRef = useRef(null);
  const recording = useRef(false);
  const audioBtnRef = useRef(null);
  const audioDomRef = useRef(null);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  // 编辑器change事件
  function messageChange() {
    setShowClear(() => {
      return editorRef.current.innerText.trim().length > 0
    })
  }
  // 清除内容
  function clearContent() {
    editorRef.current.innerText = '';
    setShowClear(false);
  }
  // 快捷发送
  function messageKeyDown(e) {
    if (e.ctrlKey && e.keyCode === 13) {
      e.preventDefault();
      document.execCommand('insertLineBreak');
    } else if (e.keyCode === 13) {
      e.preventDefault();
      sendMessage();
    }
  }
  // 发送消息
  function sendMessage() {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    let chatContent = document.getElementById('ctrl-promet').innerText;
    onSend(chatContent);
    editorRef.current.innerText = '';
    setShowClear(false);
  }
  // 设置灵感大全下拉
  function setFilterHtml(prompt, promptMap) {
    const editorDom = document.getElementById('ctrl-promet');
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
    $('body').on('click', (event) => {
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
  // 点击语音按钮
  const onRecord = async() => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    if (!recording.current) {
      window.HZRecorder.get((rec) => {
        recorderHome = rec;
        recorderHome.start();
      })
      if(!recorderHome) return
      recording.current = true;
      // 麦克风变为active样式
      audioBtnRef.current.setActive(true);
        // 开启定时器，每2秒发一次请求
        intervalData = setInterval(async() => {
          let newBlob = recorderHome?.getBlob()
          const fileOfBlob = new File([newBlob], new Date().getTime() + '.wav', {
            type:'audio/wav',
          })
          const formData = new FormData();
          formData.append('file', fileOfBlob);
          let headers = {
            'attachment-filename': encodeURI(fileOfBlob.name || ''),
          };
          if(fileOfBlob.size){
            const result = await uploadChatFile(tenantId, appId, formData, headers);
            if(result.data){
              let res = await voiceToText(tenantId,result.data.file_path,fileOfBlob.name)
              // 将data数据放入输入框中
              let inputedDate = document.getElementById('ctrl-promet').innerHTML;
              if (res.data.trim().length) {
                const editorDom = document.getElementById('ctrl-promet');
                editorDom.innerHTML = inputedDate + res.data.trim();
              }
            }
          }
        }, 2000);
    } else {
      recording.current=false
      recorderHome.stop();
      audioBtnRef.current.setActive(false);
      clearInterval(intervalData);
    }
  }
  // 停止录音
  function cancelRecord(e){
      if(recording.current&&recorderHome){
        recording.current=false
        recorderHome.stop();
        audioBtnRef.current.setActive(false);
        clearInterval(intervalData);
      }
  }
  function handleEditorClick(e) {
    if (!audioDomRef.current?.contains(e.target)) {
      recording.current
    }
  }
  useEffect(() => {
    if (showMulti) {
      dispatch(setUseMemory(true));
    } else {
      dispatch(setUseMemory(false));
    }
  }, [showMulti])
  function plays(){
    let audio =document.querySelector('#audio')
    audio.play()
  }
  function pause(){
    let audio =document.querySelector('#audio')
    audio.pause()
  }
  return <>{(
    <div className='send-editor-container' onClick={handleEditorClick}>
      <Recommends onSend={onSend}/>
      <div className='editor-inner'>
        <EditorBtnHome 
          setOpenHistory={setOpenHistory}
          clear={onClear}
          fileCallBack={fileSend}
          editorRef={editorRef}
        />
        { chatRunning && 
          <div className='editor-stop' onClick={onStop}>
            <img src='/src/assets/images/ai/stop.png' alt='' />
            <span>停止响应</span>
          </div>
        }
        <div className='editor-input' id='drop'>
          <div
            className='chat-promet-editor'
            id='ctrl-promet'
            ref={ editorRef }
            contentEditable={ true }
            onInput={messageChange}
            onKeyDown={messageKeyDown}
            onPaste={messagePaste}
          />
          <div className='send-icon' onClick={ sendMessage }>
            <SendIcon />
          </div>
          <div className='audio-icon' ref={audioDomRef} tabIndex='1' onBlur={cancelRecord} onClick={onRecord}><AudioBtn ref={audioBtnRef} /></div>
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
