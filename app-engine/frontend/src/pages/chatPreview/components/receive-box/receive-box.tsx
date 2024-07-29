
import React, { useEffect, useState, useContext } from 'react';
import { Checkbox } from 'antd';
import { useLocation } from 'react-router-dom';
import { ChatContext } from '@/pages/aippIndex/context';
import { useAppSelector } from '@/store/hook';
import Feedbacks from './feedbacks';
import MessageDetail from './message-detail';
import RuntimeForm from './runtime-form';
import SendBtn from '../send-box/send-btn';
import knowledgeBase from '@assets/images/knowledge/knowledge-base.png';
import '../../styles/receive-box.scss';

const ReciveBox = (props) => {
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
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
    filters,
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
    return <MessageDetail 
              content={content} 
              markdownSyntax={markdownSyntax} 
              finished={finished} 
              chartConfig={chartConfig}
              filters={filters}
            />
  }
  return <>{(
    <div className='receive-box'>
      {showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        {isAt ? <Img iconPath={appIcon} /> : <Img iconPath={appInfo.attributes?.icon} />}
        {isAt ? <span>{appName}</span> : <span>{appInfo.name}</span>}
      </div>
      <span className='receive-info-inner'>
        {loading ? <Loading /> : setRecieveDom(recieveType)}
        {showIcon && <SendBtn content={content} sendType='text' isRecieve={true} />}
        {showIcon && <Feedbacks logId={logId} instanceId={instanceId} feedbackStatus={feedbackStatus} refreshFeedbackStatus={props.refreshFeedbackStatus} />}
      </span>
    </div>
  )}</>
}
// 接收消息loading
const Loading = () => {
  return (
    <>
      <div className='receive-loading'>
        <div className='bounce1'></div>
        <div className='bounce2'></div>
        <div className='bounce3'></div>
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


export default ReciveBox;
