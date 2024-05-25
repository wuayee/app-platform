
import React, { useEffect, useState } from 'react';
import { Tooltip } from "antd";
import { PanleCloseIcon, PanleIcon, RebotIcon } from '@assets/icon';

// 猜你想问
const Recommends = (props) => {
  const { openClick, inspirationOpen, send } = props;
  const [ guessQuestions, setGuessQuestions ] = useState([
    "如何构建知识库",
    "我想创建一个应用",
    "推荐几个常用的应用机器人",
  ]);
  const [ visible, setVisible ] = useState(false);
  const recommendClick = (item) => {
    send(item);
  }
  const iconClick = () => {
    setVisible(false)
    openClick();
  }
  return <>{(
    <div className="recommends-inner">
      <div className="recommends-top">
        <span className="title">猜你想问</span>
        <RebotIcon />
        <span className="refresh">换一批</span>
      </div>
      <div className="recommends-list">
        <div className="list-left">
          {
            guessQuestions.map(item => {
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
