
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Spin } from 'antd';
import { Message } from '../../shared/utils/message';
import {getAippInfo, getSharedDialog} from '../../shared/http/aipp';
import ChatMessage from '../chatPreview/components/chat-message.jsx';
import robot from '../../assets/images/ai/robot1.png';
import './index.scss';


const ChatShare = () => {
  const { appId, tenantId, shareId } = useParams();
  const [ aippInfo, setAippInfo ] = useState({});
  const [ spinning, setSpinning] = useState(false);
  const [chatList ,setChatList] = useState([]);

  useEffect(() => {
    getAippDetails();
    getDialog();
  }, []);

  // 获取aipp详情
  const getAippDetails = async () => {
    setSpinning(true);
    try {
      const res = await getAippInfo(tenantId, appId);
      if (res.code === 0) {
        setAippInfo(res.data);
      }
    } finally {
      setSpinning(false);
    }
  }
  // 获取对话详情
  const getDialog = async () => {
      setSpinning(true);
      try {
          const res = await getSharedDialog(tenantId, shareId);
          if (res.code === 0) {
              const data = JSON.parse(res.data);
              const parsedItem = data.map(item => {
                  return JSON.parse(item.query);
              });
              setChatList(parsedItem);
          }
      } finally {
          setSpinning(false);
      }
  }
  return <>{(
    <div className="share-content">
      <div className="shart-inner">
        <div className='chat-share-content'>
          <div className='top'>
            <div className="head">
              <Img icon={aippInfo.attributes?.icon}/>
            </div>
            <div className="title">{ aippInfo.name }</div>
            <div className="text">{aippInfo.attributes?.description }</div>
          </div>
        </div>
          <ChatMessage
              chatList={chatList}
          />
        <Spin spinning={spinning} fullscreen />
      </div>
    </div>
  )}</>
};

const Img = (props) => {
  const { icon } = props;
  return <>{(
    <span>
      { icon ? <img src={icon}/> : <img src={robot}/> }
    </span>
  )}</>
}

export default ChatShare;
