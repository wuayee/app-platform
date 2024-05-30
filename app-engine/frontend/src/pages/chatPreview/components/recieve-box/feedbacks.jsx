
import React, { useEffect, useState } from 'react';
import { LikeIcon, UnlikeIcon, LikeSelectIcon, UnlikeSelectIcon } from '@/assets/icon';
import { Modal, Input } from "antd";
import { feedbacksRq, updateFeedback, deleteFeedback } from '@shared/http/chat';
import './styles/feedbacks.scss';
const { TextArea } = Input;

const Feedbacks = ({ logId, instanceId, feedbackStatus, refreshFeedbackStatus }) => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [textValue, setTextValue] = useState('');
  const handleOk = async () => {
    setIsModalOpen(false);
    unLikeClickConfirm();
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  const unLikeClickConfirm = async () => {
    if (feedbackStatus === -1) {
      let params = { "usrFeedback": "0", "usrFeedbackText": textValue, "instanceId": instanceId }
      await feedbacksRq(params);
    }
    else if (feedbackStatus === 0) {
      await deleteFeedback(instanceId);
    } else {
      let data = { "usrFeedback": "0", "usrFeedbackText": textValue }
      await updateFeedback(instanceId, data);
    }
    refreshFeedbackStatus(instanceId);
  }
  // 点赞
  const likeClick = async () => {
    if (feedbackStatus === -1) {
      let params = { "usrFeedback": "1", "usrFeedbackText": "", "instanceId": instanceId }
      await feedbacksRq(params);
    }
    else if (feedbackStatus === 1) {
      await deleteFeedback(instanceId);
    } else {
      let data = { "usrFeedback": "1", "usrFeedbackText": "" }
      await updateFeedback(instanceId, data);
    }
    refreshFeedbackStatus(instanceId);
  }
  // 点踩
  const unLikeClick = () => {
    setTextValue('');
    if (feedbackStatus !== 0) {
      setIsModalOpen(true);
    } else {
      unLikeClickConfirm();
    }
  }
  const onChange = (e) => {
    setTextValue(e.target.value);
  };
  return <>{(
    <div className="feed-inner">
      <div className="feed-left"></div>
      <div className="feed-right">
        {feedbackStatus !== 1 && <LikeIcon onClick={likeClick} />}
        {feedbackStatus === 1 && <LikeSelectIcon onClick={likeClick} />}
        {feedbackStatus !== 0 && <UnlikeIcon onClick={unLikeClick} />}
        {feedbackStatus === 0 && <UnlikeSelectIcon onClick={unLikeClick} />}
      </div>
      <Modal title="问题反馈" open={isModalOpen} onOk={handleOk} onCancel={handleCancel} centered>
        <TextArea rows={4} placeholder="请输入" value={textValue} onChange={onChange} />
      </Modal>
    </div>
  )}</>
};


export default Feedbacks;
