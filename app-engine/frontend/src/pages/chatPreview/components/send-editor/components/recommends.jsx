
import React, { useContext, useState, useEffect } from 'react';
import { Tooltip } from "antd";
import { AippContext } from '@/pages/aippIndex/context';
import { Message } from "@shared/utils/message";
import { PanleCloseIcon, PanleIcon, RebotIcon } from '@assets/icon';

// 猜你想问
const Recommends = (props) => {
  const { openClick, inspirationOpen, send, recommendList } = props;
  const { chatRunning } = useContext(AippContext);
  const [ visible, setVisible ] = useState(false);
  // 猜你想问
  const recommendClick = (item) => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
    send(item);
  }
  // 换一批
  const refreshClick = () => {
    if (chatRunning) {
      Message({ type: "warning", content: "对话进行中, 请稍后再试" });
      return;
    }
  }
  // 打开收起灵感大全
  const iconClick = () => {
    setVisible(false)
    openClick();
  }
  return <>{(
    <div className="recommends-inner">
      {
        recommendList && (
          <div className="recommends-top">
            <span className="title">猜你想问</span>
            <RebotIcon />
            <span className="refresh" onClick={refreshClick}>换一批</span>
          </div>
        )
      }
      <div className="recommends-list">
        <div className="list-left">
          {
            recommendList?.map(item => {
              return (
                <div className="recommends-item" onClick={recommendClick.bind(this, item)}>{item}</div>
              )
            })
          }
        </div>
        <Tooltip 
          title={ inspirationOpen ? '收起创意灵感' : '打开创意灵感' } 
          overlayInnerStyle={{color: '#212121' }}
          open={ visible }
          color="white"
        >
          <div className="list-right" 
            onClick={ iconClick } 
            onMouseEnter={() => setVisible(true)} 
            onMouseLeave={() => setVisible(false)}
          >
            { inspirationOpen ? <PanleCloseIcon /> : <PanleIcon /> }
          </div>
        </Tooltip>
      </div>
    </div>
  )}</>
}

export default Recommends;
