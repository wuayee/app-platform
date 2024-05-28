
import React, { useEffect, useState, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { Checkbox } from "antd";
import { AippContext } from '../../../aippIndex/context';
import { ChatContext } from '../../../aippIndex/context';
import Feedbacks from './feedbacks';
import MessageDetail from './message-detail';
import RuntimeForm from './runtime-form';
import SendBtn from '../send-box/send-btn';
import robot from '@assets/images/ai/robot1.png';
import '../../styles/recieve-box.scss';

const ReciveBox = (props) => {
  const { aippInfo, tenantId, appId }  = useContext(AippContext);
  const { checkCallBack, showCheck }  = useContext(ChatContext);
  const { 
    content, 
    recieveType, 
    formConfig, 
    loading, 
    checked, 
    markdownSyntax, 
    chartConfig,
    logId,
    instanceId } = props.chatItem;
  const [ showIcon, setShowIcon ] = useState(true);
  const location = useLocation();

  useEffect(() => {
    const { pathname } = location;
    if (pathname.includes('/chatShare/')) {
      setShowIcon(false);
    } 
  }, [location])
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }
  // 设置显示类型
  function setRecieveDom(type) {
    if (type === 'form') {
      return <RuntimeForm  formConfig={formConfig}/>
    }
    return <MessageDetail content={content} markdownSyntax={markdownSyntax} chartConfig={chartConfig}/> 
  }
  return <>{(
    <div className='recieve-box'>
      { showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        <Img />
        <span>{ aippInfo?.name || 'xxx' }</span>
      </div>
      <span className="recieve-info-inner">
        { loading ? <Loading /> : setRecieveDom(recieveType) }
        { showIcon && <SendBtn content={content} sendType="text" /> }
        { showIcon && <Feedbacks logId={logId} instanceId={instanceId} /> }
      </span>
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return(
   <>
    <div class="recieve-loading">
      <div class="bounce1"></div>
      <div class="bounce2"></div>
      <div class="bounce3"></div>
    </div>
   </>
  )
}
const Img = () => {
  const { aippInfo }  = useContext(AippContext);
  return <>{(
    <span>
      { aippInfo.attributes?.icon ? <img src={aippInfo.attributes.icon}/> : <img src={robot}/> }
    </span>
  )}</>
}

export default ReciveBox;
