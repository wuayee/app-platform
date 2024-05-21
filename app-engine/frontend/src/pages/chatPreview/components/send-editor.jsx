
import React, { useEffect, useState, useRef, useContext, useImperativeHandle } from 'react';
import { Upload, Checkbox, Spin, Switch } from 'antd';
import { LinkIcon, AtIcon, PanleCloseIcon, PanleIcon } from '../../../assets/icon';
import $ from 'jquery';
import exit from '@assets/images/ai/exit.png';
import talk from '@assets/images/ai/talk.png';
import file from '@assets/images/ai/file.png';
import image from '@assets/images/ai/image.png';
import audio from '@assets/images/ai/audio.png';
import stop from '@assets/images/ai/play.png';
import xiaohai from '@assets/images/ai/xiaohai2.png';
import { Message } from '../../../shared/utils/message';
import { httpUrlMap } from '../../../shared/http/httpConfig';
import { uploadChatFile } from '../../../shared/http/aipp';
import { AippContext } from '../../aippIndex/context';
import '../../../shared/utils/rendos'
import '../styles/send-editor.scss';

const docArr = [
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'text/plain',
  'application/pdf',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'application/vnd.ms-powerpoint'
]
const imgArr = ['image/jpeg', 'image/bmp', 'image/png', 'image/gif', 'image/webp', 'image/svg+xml'];
const SendEditor = (props) => {
  const { 
    onSend, 
    onClear, 
    onStop, 
    chatType, 
    filterRef, 
    requestLoading, 
    openClick,
    inspirationOpen } = props;
  const [ content, setContent ] = useState('');
  const [ selectItem, setSelectItem ] = useState({});
  const [ selectDom, setSelectDom ] = useState();
  const [ showSelect, setShowSelect ] = useState(false);
  const [ positionConfig, setPositionConfig ] = useState({});
  const { aippInfo ,chatRunning }  = useContext(AippContext);
  const editorRef = useRef(null);
  useEffect(() => {
    const dropBox = document.querySelector("#drop");
    dropBox?.addEventListener("dragenter",dragEnter,false);
    dropBox?.addEventListener("dragover",dragOver,false);
    dropBox?.addEventListener("drop",drop,false);
  }, [])
  // 编辑器change事件
  function messageChange() {
    setContent(editorRef.current.innerText);
  }
  // 快捷发送
  function messageKeyDown(e) {
    if(e.ctrlKey && e.keyCode===13){
      e.preventDefault();
      document.execCommand('insertLineBreak');
    } else if (e.keyCode===13) {
      e.preventDefault();
      sendMessage();
    }
  }
  // 粘贴文本
  function messagePaste(e) {
   e.preventDefault();
    let items = e.clipboardData?.items || [];
    for (let i = 0; i < items?.length; i++) {
      const item = items[i];
      if (item.kind === "string" && item.type === "text/plain") {
        item.getAsString(function (str) {
          document.execCommand("insertText", true, str);
        });
      } else if (item.kind === "file" && item.type.indexOf("image") !== -1) {
        const pasteFile = item.getAsFile();
      } else if (item.kind === "file") {
        const pasteFile = item.getAsFile();
      }
    }
  }
  // 发送消息
  function sendMessage() {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    let chatContent = document.getElementById('ctrl-promet').innerText;
    onSend(chatContent);
    setContent('');
    editorRef.current.innerText = '';
  }
  // 拖拽上传功能
  function dragEnter(e){
    e.stopPropagation();
    e.preventDefault();
  }
  function dragOver(e){
    e.stopPropagation();
    e.preventDefault();
  }
  // 图片拖拽回调
  function drop(e){
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    e.preventDefault();
    const { files } = e.dataTransfer;
    if (files.length && imgArr.includes(files[0].type)) {
      dragUpload(files[0], 'img');
    } else if (files.length && docArr.includes(files[0].type)) {
      dragUpload(files[0], 'doc');
    }
  }
  function dragUpload(file, type) {}
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
  //  清除节点
  function clearMove() {
    setShowSelect(false);
  }
  // 文件自动发送
  function fileSend(fileResult, fileType) {
    onSend(fileResult, fileType);
  }
  // 是否联网
  const onSwitchChange = (checked) => {}
  useImperativeHandle(filterRef, () => {
    return {
      'setFilterHtml': setFilterHtml
    }
  })
  return <>{(
    <div className='send-editor-container'>
      <Recommends openClick={openClick} inspirationOpen={inspirationOpen}/>
      <div className='editor-inner'>
        <EditorBtnHome aippInfo={aippInfo}/>
        <div className='editor-input' id="drop">
          <div
            className="chat-promet-editor"
            id="ctrl-promet"
            ref={ editorRef }
            contentEditable={ true }
            onInput={messageChange}
            onKeyDown={messageKeyDown}
            onPaste={messagePaste}
          ></div>
          <div className='send-icon' onClick={ sendMessage }></div>
          <div className='audio-icon' onClick={ sendMessage }><LinkIcon /></div>
        </div>
      </div>
      <div className='chat-tips'>
        <div className="switch-item">
          <Switch onChange={onSwitchChange} />
          <span>联网</span>
        </div>
          - 所有内容均由人工智能大模型生成，存储产品内容准确性参照存储产品文档 - 
      </div>
     { showSelect &&  (
      <EditorSelect
        chatSelectDom={selectDom}
        chatSelectItem={selectItem}
        positionConfig={positionConfig}
        clearMove={clearMove} />
     ) }
    </div>
  )}</>
};

// 编辑器操作按钮
const EditorBtn = (props) => {
  const { onClear, onStop, chatType, fileSend, requestLoading } = props;
  const [ recording, setRecording ] = useState(false);
  const { chatRunning, tenantId, appId }  = useContext(AippContext);
  const { WS_AUDIO_URL } = httpUrlMap[process.env.NODE_ENV];
  const beforeUpload = (file) => {
    return false
  }
  // 文件上传
  const onChange = async ({ file }) => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    let fileType = docArr.includes(file.type) ? 'file' : 'img';
    let headers = {
      'attachment-filename': encodeURI(file.name || '')
    }
    const formData = new FormData();
    formData.append('file', file);
    const result = await uploadChatFile(tenantId, appId, formData, headers);
    if (result.code === 0) {
      fileSend( result.data, fileType);
    } else {
      Message({ type: 'error', content: result.msg || '上传文件失败' });
    }
  }
  // 语音实时转文字
  let recorderHome = null;
  let intervalData = null;
  const onRecord = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    if (!recording) {
      window.HZRecorder.get((rec) => {
        recorderHome = rec;
        recorderHome.start();
      });
      setRecording(true);
      let conn = new WebSocket(WS_AUDIO_URL);
      conn.onopen = (evt) => {
        if (conn.readyState === 1) {
          intervalData = setInterval(() => {
            let res = recorderHome.getBlob();
            conn.send(res);
          }, 1000)
        }
      }
      conn.onmessage = (evt) => {
        if (evt.data.trim().length) {
          const editorDom = document.getElementById('ctrl-promet');
          editorDom.innerHTML = evt.data.trim();
        }
      }
      conn.onerror = (err) => {
        Message({ type: 'error', content: '语音转文字失败' })
      }
      conn.onclose = (err) => {
        recorderHome.stop();
        setRecording(false);
        clearInterval(intervalData);
      }
    }
  }
  // 退出助手
  const onExit = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return
    }
    window.parent.postMessage({ eventType: 'exit' }, '*');
  }

  return <>{(
    <div className='send-editor-btn'>
      <div className='quill-item-inner'>
        {
          !chatType && (
            <span className="quill-span quill-item-span" onClick={onExit}>
              <img src={exit} alt="" />
              <span>退出助手</span>
            </span>
          )
        }
        {
          !chatRunning ?
          (
            <Spin spinning={requestLoading} size="small">
              <span className="quill-span quill-item-span" onClick={onClear}>
                <img src={talk} alt="" />
                <span>全新对话</span>
              </span>
            </Spin>
          ) :
          (
            <Spin spinning={requestLoading} size="small">
              <span className="quill-span quill-item-span" onClick={onStop}>
                <img src={stop} alt="" />
                <span>终止对话</span>
              </span>
            </Spin>
          )
        }
      </div>
      <div className='quill-item-inner'>
        <Upload
          beforeUpload={beforeUpload}
          onChange={onChange}
          accept='.jpg,.png,.bmp,.gif.svg'>
          <span className="quill-span quill-item-span">
            <img src={image} alt="" />
            <span>上传图片</span>
          </span>
        </Upload>
        <Upload
          beforeUpload={beforeUpload}
          onChange={onChange}
          accept='.txt,.pdf,.docx,.xlsx,.mp3,.mp4'>
          <span className="quill-span quill-item-span">
            <img src={file} alt="" />
            <span>上传文件</span>
          </span>
        </Upload>
        <span
          className={["quill-span", "quill-item-span quill-last", recording ? 'recording' : null].join(' ')}
          onClick={onRecord}>
          <img src={audio} alt="" />
          { recording ? <span className="record-radius"></span> : <span>语音消息</span> }
        </span>
      </div>
    </div>
  )}</>
}

// 灵感大全下拉
const EditorSelect = (props) => {
  const {
    chatSelectItem,
    chatSelectDom,
    positionConfig,
    clearMove } = props;
  const [ selectStyle, setSelectStyle ] = useState({});
  const [ checkedList, setCheckedList ] = useState([]);
  const [ checkedNameList, setCheckedNameList ] = useState([]);
  useEffect(() => {
    const { left, top, width } = positionConfig;
    const styleObj = {
      'left': `${left - ((200 - width) / 2) }px`,
      'bottom': `${document.documentElement.clientHeight - top + 10}px`,
      'display': 'block'
    };
    setSelectStyle(styleObj);
  }, [props]);

  // 选项点击
  function selectClick(item) {
    if (!chatSelectItem.multiple ) {
      $(chatSelectDom).text(item);
      clearMove();
    }
  }
  // 多选
  function onChange(e, item) {
    let arr = [];
    let nameArr = [];
    let str = ''
    if (e.target.checked) {
      arr = [ ...checkedList, item ];
    } else {
      arr = checkedList.filter(cItem => cItem.question !== item.question);
    }
    nameArr = arr.map(item => {
      return item.question
    })
    setCheckedList(arr);
    setCheckedNameList(nameArr);
    arr.forEach(item => {
      str += `<div class='select-html'>
                <div>问题：${item.question}</div>
                <div>回答：${item.answer}</div>
              </div>`
    });
    $("#ctrl-promet").children('.select-html').remove();
    $("#ctrl-promet").append(str);
  }
  function stopClick(e) {
    e.stopPropagation();
  }
  return <>{(
    <div style={selectStyle} className='chat-select-content' onClick={stopClick}>
      { chatSelectItem.options.map((item, index) => {
        return(
          <div className="select-inner-item" key={index} onClick={selectClick.bind(this, item)}>
            { chatSelectItem.multiple ? (
              <Checkbox 
                checked={checkedNameList.includes(item.question)} 
                onChange={(e) => onChange(e, item)}
              >
                <span className="check-span" title={item.question}>
                  { item.question }
                </span>
              </Checkbox>
            ) : (<span className="normal-span" title={item}>{ item }</span>)  }
          </div>
        )
      }) }
    </div>
  )}</>
}

// 猜你想问
const Recommends = (props) => {
  const { openClick, inspirationOpen } = props;

  return <>{(
    <div className="recommends-inner">
      <div className="recommends-top">
        <span className="title">猜你想问</span>
        <span className="refresh">换一批</span>
      </div>
      <div className="recommends-list">
        <div className="list-left">
          <div className="recommends-item">如何构建知识库</div>
          <div className="recommends-item">我想创建一个应用</div>
          <div className="recommends-item">推荐几个常用的应用机器人</div>
        </div>
        <div className="list-right" onClick={openClick}>
          { inspirationOpen ? <PanleCloseIcon /> : <PanleIcon /> }
        </div>
      </div>
    </div>
  )}</>
}

// 操作按钮
const EditorBtnHome = (props) => {
  const { aippInfo } = props;

  return <>{(
    <div className="btn-inner">
      <div className="inner-left">
        <div className="inner-item">
          <img src={xiaohai} alt="" />
          <span className="item-name">{aippInfo.name || ''}</span>
          <LinkIcon />
          <AtIcon />
        </div>
      </div>
      <div className="inner-right">
        <div className="inner-item">
          <LinkIcon />
          <span className="item-name">自动</span>
          <AtIcon />
        </div>
      </div>
    </div>
  )}</>
}


export default SendEditor;
