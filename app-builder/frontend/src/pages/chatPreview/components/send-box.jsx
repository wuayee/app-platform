
import React, { useEffect, useState, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Checkbox } from 'antd';
import { trans, toClipboard } from '__shared/utils/common';
import { Message } from '__shared/utils/message';
import { picturePreview } from '../../../shared/http/aipp'
import { ChatContext } from '../../aippIndex/context';
import { httpUrlMap } from '../../../shared/http/httpConfig';
import fileImg from '../../../assets/images/ai/file2.png';
import '../styles/send-box.scss';

const SendBox = (props) => {
  const { showCheck } = props;
  const { content, checked, sendType } = props.chatItem;
  const { setShareClass, setInspiration, checkCallBack }  = useContext(ChatContext);
  const employeeNumber = localStorage.getItem('currentUserId') || null;
  // 复制
  function handleCopyQuestion() {
    content && toClipboard(content);
  }
  // 选中回调
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }
  return <>{(
    <div className='send-box'>
      { showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='send-info'>
        { sendType === 'text' ? (<div dangerouslySetInnerHTML={{ __html: trans(content) }}></div>) : (<ImgSendBox sendType={sendType} content={content} />) }
        <div className='message-tip-box-send'>
          <div className='inner'>
            {  sendType === 'text' && <div onClick={handleCopyQuestion}>复制</div> }
            {/* <div>删除</div>
            <div onClick={setShareClass}>分享问答</div>
            <div onClick={setInspiration}>添加为灵感</div> */}
          </div>
        </div>
      </div>
      <div className='user-image'>
        { employeeNumber ? <img src={`https://w3.huawei.com/w3lab/rest/yellowpage/face/${employeeNumber}/120`}/> : 
          <img src={`https://w3.huawei.com/w3lab/rest/yellowpage/face/default/120`}/>
        } 
      </div>
    </div>
  )}</>
};

const { AIPP_URL } = httpUrlMap[process.env.NODE_ENV];
const ImgSendBox = (props) => {
  const { content, sendType } = props;
  const { tenantId } = useParams();
  let { file_name, file_path } = JSON.parse(content);
  return <>{(
    <div className="img-send-box">
      { sendType === 'img' ? (
        <img className="img-send-item" src={`${AIPP_URL}/${tenantId}/file?filePath=${file_path}&fileName=${file_name}`}/>
      ) : (
        <div className="file-div-item">
          <img className="file-item" src={fileImg}/>
          <span className="file-text" title={file_name}>{file_name}</span>
        </div>
      ) }
    </div>
  )}</>
}

export default SendBox;
