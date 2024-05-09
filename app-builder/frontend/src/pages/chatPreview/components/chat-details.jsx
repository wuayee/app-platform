
import React, { useContext } from 'react';
import { AippContext } from '../../aippIndex/context';
import robot from '../../../assets/images/ai/robot1.png';
import '../styles/chat-details.scss';

const ChatDetail = () => {
  const { aippInfo }  = useContext(AippContext);
  return <>{(
    <div className='chat-details-content'>
      <div className='top'>
        <div className="head">
          <Img icon={aippInfo.attributes?.icon}/>
        </div>
        <div className="title">{ aippInfo.name }</div>
        <div className="text">{aippInfo.attributes?.description }</div>
        <div className="bottom">
          <div className="left">
            <Img icon={aippInfo.attributes?.icon}/>
          </div>
          <div className="right">
            { aippInfo.attributes?.greeting || '你好'}
          </div>
        </div>
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

export default ChatDetail;
