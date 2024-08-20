
import React, { useEffect, useState, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { Checkbox } from 'antd';
import { ChatUserIcon } from '@assets/icon';
import { trans } from '@shared/utils/common';
import { ChatContext } from '../../../aippIndex/context';
import SendBtn from './send-btn';
import ImgSendBox from './img-send-box';
import '../../styles/send-box.scss';

const SendBox = (props) => {
  const { content, checked, sendType } = props.chatItem;
  const { checkCallBack, showCheck } = useContext(ChatContext);
  const [showIcon, setShowIcon] = useState(true);
  const currentUser = localStorage.getItem('currentUser') || '';
  const location = useLocation();

  // 选中回调
  function onChange(e) {
    props.chatItem.checked = e.target.checked;
    checkCallBack();
  }
  useEffect(() => {
    const { pathname } = location;
    if (pathname.includes('/chatShare/')) {
      setShowIcon(false);
    }
  }, [location]);
  return <>{(
    <div className='send-box'>
      { showCheck && <Checkbox className='check-box' checked={checked} onChange={onChange}></Checkbox>}
      <div className='user-image'>
        <ChatUserIcon />
        <span title={currentUser}>{currentUser}</span>
      </div>
      <div className='send-info'>
        <span className='send-info-inner'>
          {sendType === 'text' ?
            (<div dangerouslySetInnerHTML={{ __html: trans(content) }}></div>) :
            (<ImgSendBox sendType={sendType} content={content} isRecieve={false} />)
          }
          {
            showIcon && <SendBtn content={content} sendType={sendType} />
          }
        </span>
      </div>
    </div>
  )}</>
};

export default SendBox;
