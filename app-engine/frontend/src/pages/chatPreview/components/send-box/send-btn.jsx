
import React, { useContext } from 'react';
import { Tooltip } from "antd";
import { toClipboard } from '@shared/utils/common';
import { ChatContext } from '../../../aippIndex/context';
import { ShareIcon, CopyIcon, DeleteIcon } from '@/assets/icon';

const SendBtn = (props) => {
  const { content, sendType } = props;
  const { setShareClass }  = useContext(ChatContext);

  // 复制
  function handleCopyQuestion() {
    content && toClipboard(content);
  }
  // tooltip隐藏
  function hideTooltip() {
    const tooltip = document.querySelectorAll('.ant-tooltip-placement-top');
    if (tooltip && tooltip.length) {
      tooltip.forEach(item => {
        item.classList.add('ant-tooltip-hidden')
      });
    }
    setShareClass();
  }
  return <>{(
    <div className='message-tip-box-send'>
      <div className='inner'>
        <Tooltip title="分享" color="white" overlayInnerStyle={{color: '#212121' }} destroyTooltipOnHide>
          <div onClick={ hideTooltip }>
            <ShareIcon />
          </div>
        </Tooltip>
        {  sendType === 'text' && 
        <Tooltip title="复制" color="white" overlayInnerStyle={{color: '#212121' }}>
          <div onClick={ handleCopyQuestion }>
            <CopyIcon />
          </div> 
        </Tooltip>
        }
        <Tooltip title="删除" color="white" overlayInnerStyle={{color: '#212121' }}>
          <div>
            <DeleteIcon />
          </div>
        </Tooltip>
      </div>
    </div>
  )}</>
};

export default SendBtn;
