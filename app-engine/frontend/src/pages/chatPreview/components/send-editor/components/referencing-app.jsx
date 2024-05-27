
import React, { useEffect, useState } from 'react';
import { MoreIcon, HistoryIcon } from '@assets/icon';
import xiaohai2 from "@assets/images/ai/xiaohai2.png";
import '../styles/referencing-app.scss'

const ReferencingApp = (props) => {
  const { atItemClick, atClick } = props;
  const [ appArr, setAppArr ] = useState([
    { name: '会议预定专家', desc: '为您安排完美的会议，从预订到组织，一切搞定。' },
    { name: '会议辅助小助手', desc: '为您的会议提供全面支持，提升效率，让您的讨论更加顺畅。' },
    { name: '经营小魔方', desc: '为您安排完美的会议，从预订到组织，一切搞定。' },
  ]);
  // 应用点击回调
  const itemClick = (item) => {
    atItemClick(item);
  }
  // 更多应用
  const moreClick = (e) => {
    e.stopPropagation();
    atClick();
  }
  return <>{(
    <div className="at-content" onClick={(e) => e.stopPropagation()}>
      <div className="at-head">
        <span className="left">历史使用的应用</span>
        <span className="right"  onClick={moreClick}>
          <MoreIcon />
          <span>更多应用</span>
        </span>
      </div>
      <div className="at-content-inner">
        {
          appArr.map((item, index) => {
            return (
              <div className="at-list-item" key={index} onClick={() => itemClick(item)}>
                <div className="left">
                  <img src={xiaohai2} alt="" />
                  <span className="name">{item.name}</span>
                  <span>{item.desc}</span>
                </div>
                <div className="right">
                  <HistoryIcon />
                </div>
              </div>
            )
          })
        }
      </div>
    </div>
  )}</>
};

export default ReferencingApp;
