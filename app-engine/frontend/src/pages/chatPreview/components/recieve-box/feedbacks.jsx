
import React, { useRef, useState } from 'react';
import { LikeIcon, UnlikeIcon } from '@/assets/icon';
import { Modal, Input } from "antd";
import { feedbacksRq } from '@shared/http/chat';
import './styles/feedbacks.scss';
const { TextArea } = Input;

const Feedbacks = ({ logId, instanceId }) => {
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ textValue, setTextValue ] = useState('');
  const handleOk = () => {
    setIsModalOpen(false);
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  // 点赞
  const likeClick = async () => {
    let params = {"logId": logId, "usrFeedback":"1", "usrFeedbackText":"xxx", "instanceId": instanceId}
    const res = await feedbacksRq(params);
  }
  // 点踩
  const onLikeClick = () => {
    setTextValue('');
    setIsModalOpen(true);
  }
  const onChange = (e) => {
    setTextValue(e.target.value);
  };
  return <>{(
    <div className="feed-inner">
      <div className="feed-left"></div>
      <div className="feed-right">
        <LikeIcon onClick={likeClick} />
        <UnlikeIcon onClick={onLikeClick}/>
      </div>
      <Modal title="问题反馈" open={isModalOpen} onOk={handleOk} onCancel={handleCancel} centered>
        <TextArea rows={4} placeholder="请输入" value={textValue} onChange={onChange}/>
      </Modal>
    </div>
  )}</>
};


export default Feedbacks;
