
import React, { useState, useEffect } from 'react';
import { Tooltip } from 'antd';
import { Message } from '@shared/utils/message';
import { PanleCloseIcon, PanleIcon, RebotIcon } from '@assets/icon';
import { getRecommends } from '@shared/http/chat';
import { useAppDispatch, useAppSelector } from '@/store/hook';
import { setInspirationOpen } from '@/store/chatStore/chatStore';

// 猜你想问
const Recommends = (props) => {
  const { onSend } = props;
  const [visible, setVisible] = useState(false);
  const [recommendList, setRecommendList] = useState([]);
  const dispatch = useAppDispatch();
  const appInfo = useAppSelector((state) => state.appStore.appInfo);
  const inspirationOpen = useAppSelector((state) => state.chatCommonStore.inspirationOpen);
  const chatList = useAppSelector((state) => state.chatCommonStore.chatList);
  const chatRunning = useAppSelector((state) => state.chatCommonStore.chatRunning);
 
  // 设置推荐列表
  function setRecommend() {
    setRecommendList([]);
    let arr = appInfo.config?.form?.properties || [];
    let recommendItem = arr.filter(item => item.name === 'recommend')[0];
    if (recommendItem) {
      setRecommendList(recommendItem.defaultValue);
    }
  }
  // 猜你想问
  const recommendClick = (item) => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    onSend(item);
  }
  // 换一批
  const refreshClick = () => {
    if (chatRunning) {
      Message({ type: 'warning', content: '对话进行中, 请稍后再试' });
      return;
    }
    if (chatList && chatList.length) {
      getRecommendList();
    }
  }
  // 获取推荐列表
  async function getRecommendList() {
    let formProp = appInfo.config.form.properties;
    let modelItem = formProp.filter(item => item.name === 'model')[0];
    if (modelItem === undefined) return;
    let model = modelItem.defaultValue || '';
    let chatLength = chatList.length;
    let question = chatList[chatLength - 2]?.content;
    let answer = chatList[chatLength - 1]?.content;
    let params = {
      question,
      answer,
      model
    }
    const res = await getRecommends(params);
    if (res.code === 0 && res.data.length > 0) {
      setRecommendList(res.data);
    }
  }
  // 打开收起灵感大全
  const iconClick = () => {
    setVisible(false);
    dispatch(setInspirationOpen(!inspirationOpen));
  }

  useEffect(() => {
    setRecommend();
  }, [appInfo]);

  // 实时刷新推荐列表
  useEffect(() => {
    if(chatList?.length>0){
      let chatItem = chatList[chatList.length - 1];
      if (chatItem && chatItem.finished && !chatItem.messageType) {
        getRecommendList();
      }
    }
  }, [chatList]);
  
  return <>{(
    <div className='recommends-inner'>
      {
        (recommendList?.length > 0) && (
          <div className='recommends-top'>
            <span className='title'>猜你想问</span>
            <RebotIcon onClick={refreshClick}/>
            <span className='refresh' onClick={refreshClick}>换一批</span>
          </div>
        )
      }
      <div className='recommends-list'>
        <div className='list-left'>
          {
            recommendList?.map((item, index) => {
              return (
                <div 
                  className='recommends-item' 
                  onClick={() => recommendClick(item)} 
                  key={index}
                >
                  {item}
                </div>
              )
            })
          }
        </div>
        <Tooltip 
          title={ inspirationOpen ? '收起创意灵感' : '打开创意灵感' } 
          overlayInnerStyle={{color: '#212121' }}
          open={ visible }
          zIndex='100'
          color='white'
        >
          <div className='list-right' 
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
