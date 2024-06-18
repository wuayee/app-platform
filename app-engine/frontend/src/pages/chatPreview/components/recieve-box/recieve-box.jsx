
import React, { useEffect, useState, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { Checkbox } from "antd";
import { ChatContext } from '@/pages/aippIndex/context';
import { urlify } from '@shared/utils/common';
import Feedbacks from './feedbacks';
import MessageDetail from './message-detail';
import RuntimeForm from './runtime-form';
import SendBtn from '../send-box/send-btn';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import '../../styles/recieve-box.scss';
import { useAppSelector } from '../../../../store/hook';

const ReciveBox = (props) => {
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const appId = useAppSelector((state) => state.appStore.appId);
  const tenantId = useAppSelector((state) => state.appStore.tenantId);
  const { checkCallBack, showCheck } = useContext(ChatContext);
  const {
    content,
    recieveType,
    formConfig,
    loading,
    checked,
    markdownSyntax,
    chartConfig,
    logId,
    instanceId,
    finished,
    feedbackStatus,
    appName,
    appIcon,
    isAt } = props.chatItem;
  const [showIcon, setShowIcon] = useState(true);
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
      return <RuntimeForm formConfig={formConfig} />
    }
    return <MessageDetail content={content} markdownSyntax={markdownSyntax} finished={finished} chartConfig={chartConfig} />
  }
  return <>{(
    <div className='recieve-box'>
      {showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        {isAt ? <Img iconPath={appIcon} /> : <Img iconPath={appInfo.attributes.icon} />}
        {isAt ? <Name name={appName} /> : <Name name={appInfo.name} />}
      </div>
      <span className="recieve-info-inner">
        {loading ? <Loading /> : setRecieveDom(recieveType)}
        {showIcon && <SendBtn content={content} sendType="text" />}
        {showIcon && <Feedbacks logId={logId} instanceId={instanceId} feedbackStatus={feedbackStatus} refreshFeedbackStatus={props.refreshFeedbackStatus} />}
      </span>
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return (
    <>
      <div className="recieve-loading">
        <div className="bounce1"></div>
        <div className="bounce2"></div>
        <div className="bounce3"></div>
      </div>
    </>
  )
}
const Img = (props) => {
  const {iconPath} = props;
  return <>{(
    <span>
      {iconPath ? <img src={iconPath} /> : <img src={knowledgeBase} />}
    </span>
  )}</>
}

const Name = (props) => {
  const {name} = props;
  return <>{(
    <span>{name}</span>
  )}</>
}

export default ReciveBox;
