
import React, { useEffect, useState } from 'react';
import { Upload, Spin } from 'antd';
import { httpUrlMap } from '@shared/http/httpConfig';
import { uploadChatFile } from '@shared/http/aipp';
import exit from '@assets/images/ai/exit.png';
import talk from '@assets/images/ai/talk.png';
import file from '@assets/images/ai/file.png';
import image from '@assets/images/ai/image.png';
import audio from '@assets/images/ai/audio.png';
import stop from '@assets/images/ai/play.png';
import { useAppSelector } from '@/store/hook';

// 编辑器操作按钮
const EditorBtn = (props) => {
  const { onClear, onStop, chatType, fileSend, requestLoading } = props;
  const [ recording, setRecording ] = useState(false);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
  const { WS_AUDIO_URL } = httpUrlMap[process.env.NODE_ENV];
  const beforeUpload = (file) => {
    return false;
  };
  // 文件上传
  const onChange = async ({ file }) => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    let fileType = docArr.includes(file.type) ? 'file' : 'img';
    let headers = {
      'attachment-filename': encodeURI(file.name || ''),
    };
    const formData = new FormData();
    formData.append('file', file);
    const result = await uploadChatFile(tenantId, appId, formData, headers);
    if (result.code === 0) {
      fileSend(result.data, fileType);
    } else {
      Message({ type: 'error', content: result.msg || '上传文件失败' });
    }
  };
  // 语音实时转文字
  let recorderHome = null;
  let intervalData = null;
  const onRecord = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
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
          }, 1000);
        }
      };
      conn.onmessage = (evt) => {
        if (evt.data.trim().length) {
          const editorDom = document.getElementById('ctrl-promet');
          editorDom.innerHTML = evt.data.trim();
        }
      };
      conn.onerror = (err) => {
        Message({ type: 'error', content: '语音转文字失败' });
      };
      conn.onclose = (err) => {
        recorderHome.stop();
        setRecording(false);
        clearInterval(intervalData);
      };
    }
  };
  // 退出助手
  const onExit = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    window.parent.postMessage({ eventType: 'exit' }, '*');
  };

  return (
    <>
      {
        <div className='send-editor-btn'>
          <div className='quill-item-inner'>
            {chatType!=='preview' && (
              <span className='quill-span quill-item-span' onClick={onExit}>
                <img src={exit} alt='' />
                <span>退出助手</span>
              </span>
            )}
            {!chatRunning ? (
              <Spin spinning={requestLoading} size='small'>
                <span className='quill-span quill-item-span' onClick={onClear}>
                  <img src={talk} alt='' />
                  <span>全新对话</span>
                </span>
              </Spin>
            ) : (
              <Spin spinning={requestLoading} size='small'>
                <span className='quill-span quill-item-span' onClick={onStop}>
                  <img src={stop} alt='' />
                  <span>终止对话</span>
                </span>
              </Spin>
            )}
          </div>
          <div className='quill-item-inner'>
            <Upload
              beforeUpload={beforeUpload}
              onChange={onChange}
              accept='.jpg,.png,.bmp,.gif.svg'
            >
              <span className='quill-span quill-item-span'>
                <img src={image} alt='' />
                <span>上传图片</span>
              </span>
            </Upload>
            <Upload
              beforeUpload={beforeUpload}
              onChange={onChange}
              accept='.txt,.pdf,.docx,.xlsx,.mp3,.mp4'
            >
              <span className='quill-span quill-item-span'>
                <img src={file} alt='' />
                <span>上传文件</span>
              </span>
            </Upload>
            <span
              className={[
                'quill-span',
                'quill-item-span quill-last',
                recording ? 'recording' : null,
              ].join(' ')}
              onClick={onRecord}
            >
              <img src={audio} alt='' />
              {recording ? (
                <span className='record-radius'></span>
              ) : (
                <span>语音消息</span>
              )}
            </span>
          </div>
        </div>
      }
    </>
  );
};

export default EditorBtn;
